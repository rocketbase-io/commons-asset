package io.rocketbase.commons.service;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.dto.batch.*;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.InvalidContentTypeException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.download.DownloadService;
import io.rocketbase.commons.util.Nulls;
import lombok.extern.slf4j.Slf4j;
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

    /**
     * will download given urls
     *
     * @param assetBatch   contains batch information per each asset
     * @param previewSizes required sizes for previews in {@link io.rocketbase.commons.dto.asset.AssetRead}
     * @return divided in succeeded and failed with key of url
     */
    public AssetBatchResult batch(AssetBatchWrite assetBatch, List<PreviewSize> previewSizes) {
        AssetBatchResult.AssetBatchResultBuilder builder = AssetBatchResult.builder();
        for (AssetBatchWriteEntry entry : assetBatch.getEntries()) {
            try {
                builder.success(entry.getUrl(), assetConverter.fromEntity(downloadOrUseCache(entry, assetBatch.getUseCache()), previewSizes));
            } catch (DownloadService.DownloadError e) {
                builder.failure(entry.getUrl(), e.getErrorCode());
            } catch (InvalidContentTypeException e) {
                builder.failure(entry.getUrl(), AssetErrorCodes.INVALID_CONTENT_TYPE);
            } catch (Exception e) {
                builder.failure(entry.getUrl(), AssetErrorCodes.UNPROCESSABLE_ASSET);
            }
        }
        return builder.build();
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
        AssetBatchResultWithoutPreviews.AssetBatchResultWithoutPreviewsBuilder builder = AssetBatchResultWithoutPreviews.builder();
        for (AssetBatchWriteEntry entry : assetBatch.getEntries()) {
            try {
                builder.success(entry.getUrl(), assetConverter.fromEntityWithoutPreviews(downloadOrUseCache(entry, assetBatch.getUseCache())));
            } catch (DownloadService.DownloadError e) {
                builder.failure(entry.getUrl(), e.getErrorCode());
            } catch (InvalidContentTypeException e) {
                builder.failure(entry.getUrl(), AssetErrorCodes.INVALID_CONTENT_TYPE);
            } catch (Exception e) {
                builder.failure(entry.getUrl(), AssetErrorCodes.UNPROCESSABLE_ASSET);
            }
        }
        return builder.build();
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
        return asset;
    }

    /**
     * will download given urls and analyse content. downloads will get deleted afterwards
     *
     * @param urls list of urls
     * @return divided in succeeded and failed with key of url
     */
    public AssetBatchAnalyseResult batchAnalyse(List<String> urls) {
        AssetBatchAnalyseResult.AssetBatchAnalyseResultBuilder builder = AssetBatchAnalyseResult.builder();
        for (String url : urls) {
            DownloadService.TempDownload download = null;
            try {
                download = downloadService.downloadUrl(url);
                AssetAnalyse analyse = assetService.analyse(download.getFile(), download.getFilename());
                analyse.setReferenceUrl(url);
                builder.success(url, analyse);
            } catch (DownloadService.DownloadError e) {
                builder.failure(url, e.getErrorCode());
            } catch (InvalidContentTypeException e) {
                builder.failure(url, AssetErrorCodes.INVALID_CONTENT_TYPE);
            } catch (Exception e) {
                builder.failure(url, AssetErrorCodes.UNPROCESSABLE_ASSET);
            } finally {
                if (download != null && download.getFile() != null) {
                    download.getFile().delete();
                }
            }
        }
        return builder.build();
    }

}
