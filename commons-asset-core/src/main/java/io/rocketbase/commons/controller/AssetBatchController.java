package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.batch.AssetBatchResult;
import io.rocketbase.commons.dto.batch.AssetBatchWrite;
import io.rocketbase.commons.service.AssetBatchService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@RestController
@ConditionalOnExpression(value = "${asset.api.batch:true}")
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetBatchController implements BaseAssetController {

    @Resource
    private AssetBatchService assetBatchService;

    @SneakyThrows
    @RequestMapping(value = "/batch", method = RequestMethod.POST)
    public AssetBatchResult processBatchFileUrls(@RequestBody @NotNull @Validated AssetBatchWrite assetBatch,
                                                 @RequestParam(required = false) MultiValueMap<String, String> params) {
        return assetBatchService.batch(assetBatch, getPreviewSizes(params), ServletUriComponentsBuilder.fromCurrentContextPath().toUriString());
    }


}
