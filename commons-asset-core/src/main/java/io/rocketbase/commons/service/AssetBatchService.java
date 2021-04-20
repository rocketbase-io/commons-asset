package io.rocketbase.commons.service;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.dto.batch.*;
import io.rocketbase.commons.event.AssetUrlAnalysed;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.InvalidContentTypeException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.download.DownloadService;
import io.rocketbase.commons.util.Nulls;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
public class AssetBatchService {

    @Resource
    private AssetConverter assetConverter;

    @Resource
    private AssetService assetService;

    @Resource
    private AssetRepository<AssetEntity> assetRepository;

    @Resource
    private DownloadService downloadService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * will download given urls
     *
     * @param assetBatch   contains batch information per each asset
     * @param previewSizes required sizes for previews in {@link io.rocketbase.commons.dto.asset.AssetRead}
     * @return divided in succeeded and failed with key of url
     */
    public AssetBatchResult batch(AssetBatchWrite assetBatch, List<PreviewSize> previewSizes) {
        AssetBatchResult result = new AssetBatchResult();
        for (AssetBatchWriteEntry entry : assetBatch.getEntries()) {
            try {
                result.addSuccess(entry.getUrl(), assetConverter.fromEntity(downloadOrUseCache(entry, assetBatch.getUseCache()), previewSizes));
            } catch (DownloadService.DownloadError e) {
                result.addFailure(entry.getUrl(), e.getErrorCode());
            } catch (InvalidContentTypeException e) {
                result.addFailure(entry.getUrl(), AssetErrorCodes.INVALID_CONTENT_TYPE);
            } catch (Exception e) {
                result.addFailure(entry.getUrl(), AssetErrorCodes.UNPROCESSABLE_ASSET);
            }
        }
        return result;
    }

    public Optional<AssetRead> migrateSingle(String url, AssetUploadMeta meta, boolean useCache) {
        try {
            AssetEntity entity = downloadOrUseCache(url, meta, useCache);
            if (entity != null) {
                return Optional.of(assetConverter.fromEntity(entity));
            }
        } catch (Exception e) {
            log.error("couldn't migrate url: {}, error: {}", url, e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * will download given urls
     *
     * @param assetBatch contains batch information per each asset
     * @return divided in succeeded and failed with key of url
     */
    public AssetBatchResultWithoutPreviews batchWithoutPreviews(AssetBatchWrite assetBatch) {
        AssetBatchResultWithoutPreviews result = new AssetBatchResultWithoutPreviews();
        for (AssetBatchWriteEntry entry : assetBatch.getEntries()) {
            try {
                result.addSuccess(entry.getUrl(), assetConverter.fromEntityWithoutPreviews(downloadOrUseCache(entry, assetBatch.getUseCache())));
            } catch (DownloadService.DownloadError e) {
                result.addFailure(entry.getUrl(), e.getErrorCode());
            } catch (InvalidContentTypeException e) {
                result.addFailure(entry.getUrl(), AssetErrorCodes.INVALID_CONTENT_TYPE);
            } catch (Exception e) {
                result.addFailure(entry.getUrl(), AssetErrorCodes.UNPROCESSABLE_ASSET);
            }
        }
        return result;
    }

    protected AssetEntity downloadOrUseCache(AssetBatchWriteEntry entry, Boolean useCache) throws Exception {
        return downloadOrUseCache(entry.getUrl(), entry, useCache);
    }

    protected AssetEntity downloadOrUseCache(String url, AssetUploadMeta meta, Boolean useCache) throws Exception {
        if (Nulls.notNull(useCache, false)) {
            Page<AssetEntity> page = assetRepository.findAll(QueryAsset.builder()
                    .referenceUrl(url)
                    .build(), PageRequest.of(0, 1));
            if (page.getTotalElements() == 1) {
                return page.getContent().get(0);
            }
        }
        DownloadService.TempDownload download = downloadService.downloadUrl(url);
        AssetEntity asset = assetService.storeAndDeleteFile(download.getFile(), download.getFilename(), download.getFile().length(), url, meta);
        applicationEventPublisher.publishEvent(new AssetUrlAnalysed(this, url, new AssetAnalyse(asset.getMeta(), asset.getType(), asset.getLqip()), asset.getId()));
        return asset;
    }

    /**
     * will download given urls and analyse content. downloads will get deleted afterwards
     *
     * @param urls list of urls
     * @return divided in succeeded and failed with key of url
     */
    public AssetBatchAnalyseResult batchAnalyse(List<String> urls) {
        AssetBatchAnalyseResult result = new AssetBatchAnalyseResult();
        for (String url : urls) {
            DownloadService.TempDownload download = null;
            try {
                download = downloadService.downloadUrl(url);
                AssetAnalyse analyse = assetService.analyse(download.getFile(), download.getFilename());
                analyse.setReferenceUrl(url);
                applicationEventPublisher.publishEvent(new AssetUrlAnalysed(this, url, analyse, null));
                result.addSuccess(url, analyse);
            } catch (DownloadService.DownloadError e) {
                result.addFailure(url, e.getErrorCode());
            } catch (InvalidContentTypeException e) {
                result.addFailure(url, AssetErrorCodes.INVALID_CONTENT_TYPE);
            } catch (Exception e) {
                result.addFailure(url, AssetErrorCodes.UNPROCESSABLE_ASSET);
            } finally {
                if (download != null && download.getFile() != null) {
                    download.getFile().delete();
                }
            }
        }
        return result;
    }

}
