package io.rocketbase.commons.service;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.dto.asset.AssetAnalyse;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.batch.*;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.InvalidContentTypeException;
import io.rocketbase.commons.model.AssetEntity;

import javax.annotation.Resource;
import java.util.List;

public class AssetBatchService {

    @Resource
    private AssetConverter assetConverter;

    @Resource
    private AssetService assetService;

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
                DownloadService.TempDownload download = downloadService.downloadUrl(entry.getUrl());
                AssetEntity asset = assetService.storeAndDeleteFile(download.getFile(), download.getFilename(), download.getFile().length(), entry.getUrl(), entry);
                builder.success(entry.getUrl(), assetConverter.fromEntity(asset, previewSizes));
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
                DownloadService.TempDownload download = downloadService.downloadUrl(entry.getUrl());
                AssetEntity asset = assetService.storeAndDeleteFile(download.getFile(), download.getFilename(), download.getFile().length(), entry.getUrl(), entry);
                builder.success(entry.getUrl(), assetConverter.fromEntityWithoutPreviews(asset));
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
