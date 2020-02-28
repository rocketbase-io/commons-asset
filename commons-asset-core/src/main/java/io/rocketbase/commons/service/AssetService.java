package io.rocketbase.commons.service;

import com.google.common.base.Stopwatch;
import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.config.AssetLqipProperties;
import io.rocketbase.commons.dto.asset.AssetAnalyse;
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

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final Tika tika = new Tika();
    private final AssetApiProperties assetApiProperties;
    private final AssetLqipProperties lqipProperties;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetRepository<AssetEntity> assetRepository;

    @Resource
    private AssetTypeFilterService assetTypeFilterService;

    @Resource
    private ImagePreviewRendering imagePreviewRendering;

    public AssetEntity store(InputStream inputStream, String originalFilename, long size, String systemRefId, String context) {
        return store(inputStream, originalFilename, size, systemRefId, context, null);
    }

    public AssetEntity store(InputStream inputStream, String originalFilename, long size, String systemRefId, String context, String referenceUrl) {
        return store(inputStream, originalFilename, size, systemRefId, context, referenceUrl, null);
    }

    public AssetEntity store(InputStream inputStream, String originalFilename, long size, String systemRefId, String context, String referenceUrl, Map<String, String> keyValues) {
        try {
            Stopwatch stopwatch = null;
            if (log.isDebugEnabled()) {
                stopwatch = Stopwatch.createStarted();
            }

            String suffix = "";
            if (originalFilename
                    .contains(".")) {
                suffix = originalFilename
                        .substring(originalFilename
                                .lastIndexOf('.'));
            }
            File tempFile = File.createTempFile("asset", suffix);
            IOUtils.copy(inputStream, new FileOutputStream(tempFile));

            AssetEntity asset = storeAndDeleteFile(tempFile, originalFilename, size, systemRefId, context, referenceUrl, keyValues);

            if (log.isDebugEnabled()) {
                log.debug("store file {} with id: {}, took: {} ms", originalFilename, asset.getId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }

            return asset;
        } catch (IOException e) {
            log.error("handleFileUpload error: {}", e.getMessage());
            throw new UnprocessableAssetException();
        }
    }

    /**
     * will add/update key values and removes those with value = null<br>
     * is only an update so that not mentioned keys will still be saved within entity
     */
    public AssetEntity updateKeyValues(AssetEntity asset, Map<String, String> keyValues) {
        AssetEntity entity = assetRepository.findById(asset.getId())
                .orElseThrow(NotFoundException::new);
        handleKeyValues(entity, keyValues);
        return assetRepository.save(entity);
    }

    protected void handleKeyValues(AssetEntity entity, Map<String, String> keyValues) {
        if (keyValues != null) {
            keyValues.forEach((key, value) -> {
                if (value != null) {
                    entity.addKeyValue(key, value);
                } else {
                    entity.removeKeyValue(key);
                }
            });
        }
    }

    public AssetEntity storeAndDeleteFile(File file, String originalFilename, long size, String systemRefId, String context, String referenceUrl, Map<String, String> keyValues) throws IOException {
        try {
            AssetType assetType = detectAssetTypeWithChecks(file, originalFilename, size, systemRefId, context, referenceUrl);
            AssetEntity asset = saveAndUploadAsset(assetType, file, originalFilename, size, systemRefId, context, referenceUrl, keyValues);
            return asset;
        } finally {
            file.delete();
        }
    }

    private AssetType detectAssetTypeWithChecks(File file, String originalFilename, long size, String systemRefId, String context, String referenceUrl) throws IOException {
        String contentType = tika.detect(file);
        AssetType assetType = AssetType.findByContentType(contentType);
        if (assetType == null) {
            log.info("detected contentType: {}", contentType);
            throw new InvalidContentTypeException(contentType);
        } else if (!assetTypeFilterService.isAllowed(assetType, new AssetUploadDetail(file, originalFilename, size, systemRefId, context, referenceUrl))) {
            log.info("got assetType: {} that is not within allowed list: {}", assetType, assetTypeFilterService.getAllowedAssetTypes());
            throw new NotAllowedAssetTypeException(assetType);
        }
        return assetType;
    }

    public Optional<AssetEntity> findByIdOrSystemRefId(String sid) {
        return assetRepository.findByIdOrSystemRefId(sid);
    }

    public Optional<AssetEntity> findById(String id) {
        return assetRepository.findById(id);
    }

    public void deleteByIdOrSystemRefId(String sid) {
        AssetEntity asset = assetRepository.findByIdOrSystemRefId(sid)
                .orElseThrow(NotFoundException::new);

        fileStorageService.delete(asset);
        assetRepository.delete(asset.getId());
    }

    private AssetEntity saveAndUploadAsset(AssetType type, File file, String originalFilename, long size, String systemRefId, String context, String referenceUrl, Map<String, String> keyValues) {

        if (systemRefId != null) {
            if (assetRepository.findBySystemRefId(systemRefId).isPresent()) {
                throw new SystemRefIdAlreadyUsedException();
            }
        }

        AnalyseResult analyse = analyse(type, file);

        AssetEntity entity = assetRepository.initNewInstance();
        entity.setType(type);
        entity.setOriginalFilename(originalFilename);
        entity.setReferenceUrl(referenceUrl);
        entity.setSystemRefId(systemRefId);
        entity.setContext(context);
        entity.setFileSize(size);
        entity.setResolution(analyse.getResolution());
        entity.setColorPalette(analyse.getColorPalette());

        handleKeyValues(entity, keyValues);

        try {
            fileStorageService.upload(entity, file);

            if (lqipProperties.isEnabled() && type.isImage()) {
                try {
                    entity.setLqip(imagePreviewRendering.getLqip(type, new FileInputStream(file)));
                } catch (Exception e) {
                    log.error("got problem with lqip generation. {}", e.getMessage());
                }
            }

            assetRepository.save(entity);
        } catch (Exception e) {
            log.error("couldn't upload entity. {}", e.getMessage());
            throw new UnprocessableAssetException();
        }

        return entity;
    }

    public AssetAnalyse analyse(File file, String originalFilename) throws IOException {
        AssetAnalyse result = null;
        long fileSize = file.length();
        AssetType assetType = detectAssetTypeWithChecks(file, originalFilename, fileSize, null, null, null);
        AnalyseResult analyse = analyse(assetType, file);

        result = AssetAnalyse.builderAnalyse()
                .type(assetType)
                .fileSize(fileSize)
                .resolution(analyse.getResolution())
                .colorPalette(analyse.getColorPalette())
                .created(Instant.now())
                .originalFilename(originalFilename)
                .build();
        return result;
    }

    private AnalyseResult analyse(AssetType type, File file) {
        AnalyseResult.AnalyseResultBuilder builder = AnalyseResult.builder();
        if (type.isImage() && (assetApiProperties.isDetectResolution() || assetApiProperties.isDetectColors())) {
            try {
                BufferedImage bufferedImage = ImageIO.read(file);
                if (assetApiProperties.isDetectResolution()) {
                    if (bufferedImage != null) {
                        builder.resolution(new Resolution(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    } else {
                        log.trace("file not readable");
                    }
                }
                if (assetApiProperties.isDetectColors()) {
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
    public static class AnalyseResult {
        private Resolution resolution;
        private ColorPalette colorPalette;
    }
}
