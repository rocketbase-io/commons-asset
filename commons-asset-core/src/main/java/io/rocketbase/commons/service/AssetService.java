package io.rocketbase.commons.service;

import com.google.common.base.Stopwatch;
import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.event.*;
import io.rocketbase.commons.exception.*;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetTypeFilterService.AssetUploadDetail;
import io.rocketbase.commons.service.handler.AssetHandler;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class AssetService {

    private final Tika tika = new Tika();
    private final AssetApiProperties assetApiProperties;

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetRepository<AssetEntity> assetRepository;

    @Resource
    private AssetTypeFilterService assetTypeFilterService;

    @Resource
    private AssetHandler assetHandler;

    @Resource
    private OriginalUploadModifier originalUploadModifier;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public AssetEntity store(InputStream inputStream, String originalFilename, long size, String systemRefId, String context) {
        return store(inputStream, originalFilename, size, null, DefaultAssetUploadMeta.builder()
                .systemRefId(systemRefId)
                .context(context)
                .build()
        );
    }

    public AssetEntity store(InputStream inputStream, String originalFilename, long size, String referenceUrl, AssetUploadMeta uploadMeta) {
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

            AssetEntity asset = storeAndDeleteFile(tempFile, originalFilename, size, referenceUrl, uploadMeta);

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
    public AssetEntity update(AssetEntity asset, AssetUpdate update) {
        handleKeyValues(asset, update.getKeyValues());
        if (!StringUtils.isEmpty(update.getSystemRefId()) && assetRepository.findBySystemRefId(update.getSystemRefId()).isPresent()) {
            throw new SystemRefIdAlreadyUsedException();
        }
        asset.setSystemRefId(update.getSystemRefId());
        asset.setEol(update.getEol());

        applicationEventPublisher.publishEvent(new AssetUpdateMetaEvent(this, asset));
        return assetRepository.save(asset);
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

    public AssetEntity storeAndDeleteFile(File file, String originalFilename, long size, String referenceUrl, AssetUploadMeta uploadMeta) throws IOException {
        try {
            AssetType assetType = detectAssetTypeWithChecks(file, originalFilename, size, referenceUrl, uploadMeta);
            AssetEntity asset = saveAndUploadAsset(assetType, file, originalFilename, size, referenceUrl, uploadMeta);
            return asset;
        } finally {
            file.delete();
        }
    }

    private AssetType detectAssetTypeWithChecks(File file, String originalFilename, long size, String referenceUrl, AssetUploadMeta uploadMeta) throws IOException {
        String contentType = tika.detect(file);
        AssetType assetType = AssetType.findByContentType(contentType);
        if (assetType == null) {
            log.info("detected contentType: {}", contentType);
            throw new InvalidContentTypeException(contentType);
        } else if (!assetTypeFilterService.isAllowed(assetType, new AssetUploadDetail(file, originalFilename, size, referenceUrl, uploadMeta))) {
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

        applicationEventPublisher.publishEvent(new AssetDeleteEvent(this, asset));

        fileStorageService.delete(asset);
        assetRepository.delete(asset.getId());
    }

    private AssetEntity saveAndUploadAsset(AssetType type, File file, String originalFilename, long size, String referenceUrl, AssetUploadMeta uploadMeta) {

        if (Nulls.notNull(uploadMeta, AssetUploadMeta::getSystemRefId, null) != null) {
            if (assetRepository.findBySystemRefId(uploadMeta.getSystemRefId()).isPresent()) {
                throw new SystemRefIdAlreadyUsedException();
            }
        }

        AssetAnalyse analyse = assetHandler.getAnalyse(type, file, originalFilename);
        OriginalUploadModifier.Modification modification = originalUploadModifier.modifyUploadBeforeSave(analyse, file, uploadMeta);

        AssetEntity entity = assetRepository.initNewInstance();
        entity.setType(type);
        entity.setOriginalFilename(originalFilename);
        entity.setReferenceUrl(referenceUrl);
        entity.setSystemRefId(Nulls.notNull(uploadMeta, AssetUploadMeta::getSystemRefId, null));
        entity.setContext(Nulls.notNull(uploadMeta, AssetUploadMeta::getContext, null));
        entity.setFileSize(modification.getAnalyse().getFileSize());
        entity.setResolution(modification.getAnalyse().getResolution());
        entity.setColorPalette(modification.getAnalyse().getColorPalette());
        entity.setEol(Nulls.notNull(uploadMeta, AssetUploadMeta::getEol, null));
        entity.setLqip(modification.getAnalyse().getLqip());

        handleKeyValues(entity, Nulls.notNull(uploadMeta, AssetUploadMeta::getKeyValues, null));

        applicationEventPublisher.publishEvent(new AssetUploadEvent(this, entity, modification));

        try {
            fileStorageService.upload(entity, modification.getFile());
            assetRepository.save(entity);

            applicationEventPublisher.publishEvent(new AssetAfterUploadEvent(this, entity, modification));
            if (!modification.getFile().equals(file)) {
                modification.getFile().delete();
            }
        } catch (Exception e) {
            log.error("couldn't upload entity. {}", e.getMessage());
            throw new UnprocessableAssetException();
        }

        return entity;
    }

    public AssetAnalyse analyse(File file, String originalFilename) throws IOException {
        AssetType assetType = detectAssetTypeWithChecks(file, originalFilename, file.length(), null, null);
        return assetHandler.getAnalyse(assetType, file, originalFilename);
    }

    public AssetEntity copyByIdOrSystemRefId(String sid) {
        AssetEntity source = assetRepository.findByIdOrSystemRefId(sid)
                .orElseThrow(NotFoundException::new);

        AssetEntity target = assetRepository.initNewInstance();
        BeanUtils.copyProperties(source, target, "id", "created");

        applicationEventPublisher.publishEvent(new AssetCopyEvent(this, source, target));

        fileStorageService.copy(source, target);
        return assetRepository.save(target);
    }

}
