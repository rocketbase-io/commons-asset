package io.rocketbase.commons.controller;

import com.google.common.base.Stopwatch;
import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.exception.EmptyFileException;
import io.rocketbase.commons.exception.InvalidContentTypeException;
import io.rocketbase.commons.exception.SystemRefIdAlreadyUsedException;
import io.rocketbase.commons.exception.UnprocessableAssetException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("${asset.api.endpoint:/api/asset}")
@Slf4j
public class AssetController implements BaseController {


    private final Tika tika = new Tika();

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetRepository assetRepository;

    @Resource
    private AssetConverter assetConverter;


    @RequestMapping(method = RequestMethod.POST)
    public AssetRead handleFileUpload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "systemRefId", required = false) String systemRefId,
                                      HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }
        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            InputStream inputStream = file.getInputStream();
            String suffix = "";
            if (file.getOriginalFilename()
                    .contains(".")) {
                suffix = file.getOriginalFilename()
                        .substring(file.getOriginalFilename()
                                .lastIndexOf('.'));
            }
            File tempFile = File.createTempFile("asset", suffix);
            AssetType assetType = null;
            try {
                IOUtils.copy(inputStream, new FileOutputStream(tempFile));

                String contentType = tika.detect(tempFile);
                assetType = AssetType.findByContentType(contentType);
                if (assetType == null) {
                    log.info("detected contentType: {}", contentType);
                    throw new InvalidContentTypeException(contentType);
                }

                String originalFilename = file.getOriginalFilename();
                long size = file.getSize();

                AssetEntity asset = saveAndUploadAsset(assetType, tempFile, originalFilename, null, size, systemRefId);
                log.debug("uploaded file {} with id: {}, took: {} ms", originalFilename, asset.getId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));

                return assetConverter.fromEntity(asset, getPreviewSizes(null), request);
            } finally {
                tempFile.delete();
            }
        } catch (IOException e) {
            log.error("handleFileUpload error: {}", e.getMessage());
            throw new UnprocessableAssetException();
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public PageableResult<AssetRead> findAll(@RequestParam(required = false) MultiValueMap<String, String> params, HttpServletRequest request) {
        Page<AssetEntity> pageResult = assetRepository.findAll(parsePageRequest(params));
        return PageableResult.contentPage(assetConverter.fromEntities(pageResult.getContent(), getPreviewSizes(params), request), pageResult);
    }

    @RequestMapping(value = "/{sid}", method = RequestMethod.GET)
    public AssetRead getAsset(@PathVariable("sid") String sid, @RequestParam(required = false) MultiValueMap<String, String> params, HttpServletRequest request) {
        return assetConverter.fromEntity(assetRepository.getByIdOrSystemRefId(sid), getPreviewSizes(params), request);
    }

    /**
     * used to get raw content<br>
     * thumbor should get access directly via mongo-connector or s3-connector
     *
     * @param sid id or systemRefId of asset
     * @return content-stream
     */
    @RequestMapping(value = "/{sid}/b", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getAsset(@PathVariable("sid") String sid) {
        AssetEntity entity = assetRepository.getByIdOrSystemRefId(sid);
        InputStreamResource streamResource = fileStorageService.download(entity);

        return ResponseEntity.ok()
                .contentLength(entity.getFileSize())
                .contentType(MediaType.parseMediaType(entity.getType().getContentType()))
                .body(streamResource);
    }

    @RequestMapping(value = "/{sid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAsset(@PathVariable("sid") String sid) {
        AssetEntity asset = assetRepository.getByIdOrSystemRefId(sid);

        fileStorageService.delete(asset);
        assetRepository.delete(asset.getId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private AssetEntity saveAndUploadAsset(AssetType type, File file, String originalFilename, String referenceUrl, long size, String systemRefId) {

        if (systemRefId != null) {
            if (assetRepository.findBySystemRefId(systemRefId) != null) {
                throw new SystemRefIdAlreadyUsedException();
            }
        }

        AssetEntity entity = AssetEntity.builder()
                .id(ObjectId.get().toHexString())
                .type(type)
                .originalFilename(originalFilename)
                .referenceUrl(referenceUrl)
                .systemRefId(systemRefId)
                .fileSize(size)
                .created(LocalDateTime.now())
                .build();

        if (type.isImage()) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                if (bufferedImage != null) {
                    entity.setResolution(new Resolution(bufferedImage.getWidth(), bufferedImage.getHeight()));
                } else {
                    log.trace("file not readable");
                }
            } catch (Exception e) {
                log.error("could not read file information from entity {}", entity);
            }
        }

        try {
            fileStorageService.upload(entity, file);
            assetRepository.save(entity);
        } catch (Exception e) {
            log.error("couldn't upload entity. {}", e.getMessage());
            throw new UnprocessableAssetException();
        }

        return entity;
    }

    private List<PreviewSize> getPreviewSizes(MultiValueMap<String, String> params) {
        if (params != null && params.containsKey("size")) {
            Set<PreviewSize> previewSizes = new TreeSet<>();
            for (String val : params.get("size")) {
                previewSizes.add(PreviewSize.getByName(val, PreviewSize.S));
            }
            return new ArrayList<>(previewSizes);
        } else {
            return Arrays.asList(PreviewSize.S, PreviewSize.M, PreviewSize.L);
        }
    }


}
