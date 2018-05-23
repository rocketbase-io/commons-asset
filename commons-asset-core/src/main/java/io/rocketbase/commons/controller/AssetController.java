package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.exception.EmptyFileException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.AssetService;
import io.rocketbase.commons.service.FileStorageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("${asset.api.endpoint:/api/asset}")
@Slf4j
public class AssetController implements BaseController {

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetRepository assetRepository;

    @Resource
    private AssetConverter assetConverter;

    @Resource
    private AssetService assetService;

    @SneakyThrows
    @RequestMapping(method = RequestMethod.POST)
    public AssetRead handleFileUpload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "systemRefId", required = false) String systemRefId,
                                      HttpServletRequest request) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        AssetEntity asset = assetService.store(file.getInputStream(), file.getOriginalFilename(), file.getSize(), systemRefId);

        return assetConverter.fromEntity(asset, getPreviewSizes(null), getBaseUrl(request));
    }

    @RequestMapping(method = RequestMethod.GET)
    public PageableResult<AssetRead> findAll(@RequestParam(required = false) MultiValueMap<String, String> params, HttpServletRequest request) {
        Page<AssetEntity> pageResult = assetRepository.findAll(parsePageRequest(params));
        return PageableResult.contentPage(assetConverter.fromEntities(pageResult.getContent(), getPreviewSizes(params), getBaseUrl(request)), pageResult);
    }

    @RequestMapping(value = "/{sid}", method = RequestMethod.GET)
    public AssetRead getAsset(@PathVariable("sid") String sid, @RequestParam(required = false) MultiValueMap<String, String> params, HttpServletRequest request) {
        return assetConverter.fromEntity(assetService.getByIdOrSystemRefId(sid), getPreviewSizes(params), getBaseUrl(request));
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
    public ResponseEntity<InputStreamResource> downloadAsset(@PathVariable("sid") String sid) {
        AssetEntity entity = assetRepository.getByIdOrSystemRefId(sid);
        InputStreamResource streamResource = fileStorageService.download(entity);

        return ResponseEntity.ok()
                .contentLength(entity.getFileSize())
                .contentType(MediaType.parseMediaType(entity.getType().getContentType()))
                .body(streamResource);
    }

    @RequestMapping(value = "/{sid}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAsset(@PathVariable("sid") String sid) {
        assetService.deleteByIdOrSystemRefId(sid);

        return new ResponseEntity<>(HttpStatus.OK);
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