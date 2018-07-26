package io.rocketbase.commons.service;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.dto.batch.AssetBatchResult;
import io.rocketbase.commons.dto.batch.AssetBatchWrite;
import io.rocketbase.commons.dto.batch.AssetBatchWriteEntry;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.InvalidContentTypeException;
import io.rocketbase.commons.model.AssetEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
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
     * @param baseUrl      needed for internal preview service
     * @return divided in succeeded and failed with key of url
     */
    public AssetBatchResult batch(AssetBatchWrite assetBatch, List<PreviewSize> previewSizes, String baseUrl) {
        return processBatch(assetBatch, true, previewSizes, baseUrl);
    }

    /**
     * will download given urls
     *
     * @param assetBatch contains batch information per each asset
     * @return divided in succeeded and failed with key of url<br>
     * {@link io.rocketbase.commons.dto.asset.AssetRead} doesn't have previews!
     */
    public AssetBatchResult batchWithoutPreviews(AssetBatchWrite assetBatch) {
        return processBatch(assetBatch, false, null, null);
    }

    private AssetBatchResult processBatch(AssetBatchWrite assetBatch, boolean withPreviews, List<PreviewSize> previewSizes, String baseUrl) {
        AssetBatchResult.AssetBatchResultBuilder builder = AssetBatchResult.builder();
        for (AssetBatchWriteEntry entry : assetBatch.getEntries()) {
            try {
                DownloadService.TempDownload download = downloadService.downloadUrl(entry.getUrl());
                AssetEntity asset = assetService.storeAndDeleteFile(download.getFile(), download.getFilename(), download.getFile().length(), entry.getSystemRefId(), entry.getUrl());
                if (withPreviews) {
                    builder.success(entry.getUrl(), assetConverter.fromEntity(asset, previewSizes, baseUrl));
                } else {
                    builder.success(entry.getUrl(), assetConverter.fromEntityWithoutPreviews(asset));
                }
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

}
