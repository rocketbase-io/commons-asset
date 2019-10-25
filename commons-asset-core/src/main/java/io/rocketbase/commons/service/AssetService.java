package io.rocketbase.commons.service;

import com.google.common.base.Stopwatch;
import io.rocketbase.commons.config.ApiProperties;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
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

            AssetEntity asset = storeAndDeleteFile(tempFile, originalFilename, size, systemRefId, context, referenceUrl);

            if (log.isDebugEnabled()) {
                log.debug("store file {} with id: {}, took: {} ms", originalFilename, asset.getId(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }

            return asset;
        } catch (IOException e) {
            log.error("handleFileUpload error: {}", e.getMessage());
            throw new UnprocessableAssetException();
        }
    }

    public AssetEntity storeAndDeleteFile(File file, String originalFilename, long size, String systemRefId, String context, String referenceUrl) throws IOException {
        try {
            AssetType assetType = detectAssetTypeWithChecks(file, originalFilename, size, systemRefId, context, referenceUrl);
            AssetEntity asset = saveAndUploadAsset(assetType, file, originalFilename, size, systemRefId, context, referenceUrl);
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
        Optional<AssetEntity> asset = assetRepository.findByIdOrSystemRefId(sid);

        if (!asset.isPresent()) {
            throw new NotFoundException();
        }

        fileStorageService.delete(asset.get());
        assetRepository.delete(asset.get().getId());
    }

    private AssetEntity saveAndUploadAsset(AssetType type, File file, String originalFilename, long size, String systemRefId, String context, String referenceUrl) {

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

        try {
            fileStorageService.upload(entity, file);
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
    public static class AnalyseResult {
        private Resolution resolution;
        private ColorPalette colorPalette;
    }
}
