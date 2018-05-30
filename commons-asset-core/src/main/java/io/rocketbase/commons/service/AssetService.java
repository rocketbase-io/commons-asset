package io.rocketbase.commons.service;

import com.google.common.base.Stopwatch;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.exception.InvalidContentTypeException;
import io.rocketbase.commons.exception.NotAllowedAssetTypeException;
import io.rocketbase.commons.exception.SystemRefIdAlreadyUsedException;
import io.rocketbase.commons.exception.UnprocessableAssetException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetTypeFilterService.AssetUploadDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AssetService {

    private final Tika tika = new Tika();

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetRepository assetRepository;

    @Resource
    private AssetTypeFilterService assetTypeFilterService;

    public AssetEntity store(InputStream inputStream, String originalFilename, long size, String systemRefId) {
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();

            String suffix = "";
            if (originalFilename
                    .contains(".")) {
                suffix = originalFilename
                        .substring(originalFilename
                                .lastIndexOf('.'));
            }
            File tempFile = File.createTempFile("asset", suffix);
            IOUtils.copy(inputStream, new FileOutputStream(tempFile));

            AssetEntity asset = storeAndDeleteFile(tempFile, originalFilename, size, systemRefId, null);

            log.debug("store file {} with id: {}, took: {} ms", originalFilename, asset.getId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));

            return asset;
        } catch (IOException e) {
            log.error("handleFileUpload error: {}", e.getMessage());
            throw new UnprocessableAssetException();
        }
    }

    public AssetEntity storeAndDeleteFile(File file, String originalFilename, long size, String systemRefId, String referenceUrl) throws IOException {
        try {
            String contentType = tika.detect(file);
            AssetType assetType = AssetType.findByContentType(contentType);
            if (assetType == null) {
                log.info("detected contentType: {}", contentType);
                throw new InvalidContentTypeException(contentType);
            } else if (!assetTypeFilterService.isAllowed(assetType, new AssetUploadDetail(file, originalFilename, size, systemRefId, referenceUrl))) {
                log.info("got assetType: {} that is not within allowed list: {}", assetType, assetTypeFilterService.getAllowedAssetTypes());
                throw new NotAllowedAssetTypeException(assetType);
            }
            AssetEntity asset = saveAndUploadAsset(assetType, file, originalFilename, referenceUrl, size, systemRefId);
            return asset;
        } finally {
            file.delete();
        }
    }

    public AssetEntity getByIdOrSystemRefId(String sid) {
        return assetRepository.getByIdOrSystemRefId(sid);
    }

    public void deleteByIdOrSystemRefId(String sid) {
        AssetEntity asset = assetRepository.getByIdOrSystemRefId(sid);

        fileStorageService.delete(asset);
        assetRepository.delete(asset.getId());
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
}
