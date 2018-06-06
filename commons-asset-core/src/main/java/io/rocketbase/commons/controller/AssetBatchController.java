package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.dto.batch.AssetBatchResult;
import io.rocketbase.commons.dto.batch.AssetBatchWrite;
import io.rocketbase.commons.dto.batch.AssetBatchWriteEntry;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.InvalidContentTypeException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetService;
import io.rocketbase.commons.service.DownloadService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@ConditionalOnExpression(value = "${asset.api.batch:true}")
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetBatchController implements BaseAssetController {

    @Resource
    private AssetConverter assetConverter;

    @Resource
    private AssetService assetService;

    @Resource
    private DownloadService downloadService;

    @SneakyThrows
    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    public AssetBatchResult processBatchFileUrls(@RequestBody @NotNull @Validated AssetBatchWrite assetBatch,
                                                 @RequestParam(required = false) MultiValueMap<String, String> params) {

        AssetBatchResult.AssetBatchResultBuilder builder = AssetBatchResult.builder();
        for (AssetBatchWriteEntry entry : assetBatch.getEntries()) {
            try {
                DownloadService.TempDownload download = downloadService.downloadUrl(entry.getUrl());
                AssetEntity asset = assetService.storeAndDeleteFile(download.getFile(), download.getFilename(), download.getFile().length(), entry.getSystemRefId(), entry.getUrl());
                builder.success(entry.getUrl(), assetConverter.fromEntityByRequestContext(asset, getPreviewSizes(params)));
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
