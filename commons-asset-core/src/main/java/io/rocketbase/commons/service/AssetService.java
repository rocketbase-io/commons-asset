package io.rocketbase.commons.service;

import com.google.common.base.Stopwatch;
import io.rocketbase.commons.config.ApiProperties;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.ColorPalette;
import io.rocketbase.commons.dto.asset.Resolution;
import io.rocketbase.commons.exception.*;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetTypeFilterService.AssetUploadDetail;
import io.rocketbase.commons.tooling.ColorDetection;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.bson.types.ObjectId;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final Tika tika = new Tika();
    private final ApiProperties apiProperties;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetRepository assetRepository;

    @Resource
    private AssetTypeFilterService assetTypeFilterService;

    public AssetEntity store(InputStream inputStream, String originalFilename, long size, String systemRefId, String context) {
        return store(inputStream, originalFilename, size, systemRefId, context, null);
    }

    public AssetEntity store(InputStream inputStream, String originalFilename, long size, String systemRefId, String context, String referenceUrl) {
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

            AssetEntity asset = storeAndDeleteFile(tempFile, originalFilename, size, systemRefId, context, referenceUrl);

            log.debug("store file {} with id: {}, took: {} ms", originalFilename, asset.getId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));

            return asset;
        } catch (IOException e) {
            log.error("handleFileUpload error: {}", e.getMessage());
            throw new UnprocessableAssetException();
        }
    }

    public AssetEntity storeAndDeleteFile(File file, String originalFilename, long size, String systemRefId, String context, String referenceUrl) throws IOException {
        try {
            String contentType = tika.detect(file);
            AssetType assetType = AssetType.findByContentType(contentType);
            if (assetType == null) {
                log.info("detected contentType: {}", contentType);
                throw new InvalidContentTypeException(contentType);
            } else if (!assetTypeFilterService.isAllowed(assetType, new AssetUploadDetail(file, originalFilename, size, systemRefId, context, referenceUrl))) {
                log.info("got assetType: {} that is not within allowed list: {}", assetType, assetTypeFilterService.getAllowedAssetTypes());
                throw new NotAllowedAssetTypeException(assetType);
            }
            AssetEntity asset = saveAndUploadAsset(assetType, file, originalFilename, size, systemRefId, context, referenceUrl);
            return asset;
        } finally {
            file.delete();
        }
    }

    public Optional<AssetEntity> findByIdOrSystemRefId(String sid) {
        return assetRepository.findByIdOrSystemRefId(sid);
    }

    public Optional<AssetEntity> findById(String id) {
        return assetRepository.findById(id);
    }

    public void deleteByIdOrSystemRefId(String sid) {
        AssetEntity asset = assetRepository.findByIdOrSystemRefId(sid)
                .orElseThrow(() -> new NotFoundException());

        fileStorageService.delete(asset);
        assetRepository.delete(asset.getId());
    }

    private AssetEntity saveAndUploadAsset(AssetType type, File file, String originalFilename, long size, String systemRefId, String context, String referenceUrl) {

        if (systemRefId != null) {
            if (assetRepository.findBySystemRefId(systemRefId).isPresent()) {
                throw new SystemRefIdAlreadyUsedException();
            }
        }

        AssetAnalyse analyse = analyse(type, file);

        AssetEntity entity = AssetEntity.builder()
                .id(ObjectId.get().toHexString())
                .type(type)
                .originalFilename(originalFilename)
                .referenceUrl(referenceUrl)
                .systemRefId(systemRefId)
                .context(context)
                .fileSize(size)
                .created(LocalDateTime.now())
                .resolution(analyse.getResolution())
                .colorPalette(analyse.getColorPalette())
                .build();

        try {
            fileStorageService.upload(entity, file);
            assetRepository.save(entity);
        } catch (Exception e) {
            log.error("couldn't upload entity. {}", e.getMessage());
            throw new UnprocessableAssetException();
        }

        return entity;
    }

    private AssetAnalyse analyse(AssetType type, File file) {
        AssetAnalyse.AssetAnalyseBuilder builder = AssetAnalyse.builder();
        if (type.isImage() && (apiProperties.isDetectResolution() || apiProperties.isDetectColors())) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                if (apiProperties.isDetectResolution()) {
                    if (bufferedImage != null) {
                        builder.resolution(new Resolution(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    } else {
                        log.trace("file not readable");
                    }
                }
                if (apiProperties.isDetectColors()) {
                    builder.colorPalette(ColorDetection.detect(bufferedImage));
                }
            } catch (Exception e) {
                log.error("could not read file information from file {}", file.getPath());
            }
        }
        return builder.build();
    }

    @Builder
    @Getter
    public static class AssetAnalyse {
        private Resolution resolution;
        private ColorPalette colorPalette;
    }
}
