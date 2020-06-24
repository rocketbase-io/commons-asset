package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.converter.QueryPreviewSizeConverter;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@ConditionalOnExpression(value = "${asset.api.copy:true}")
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetCopyController implements BaseController {

    @Resource
    private AssetService assetService;

    @Resource
    private AssetConverter assetConverter;

    @RequestMapping(value = "/{sid}/copy", method = RequestMethod.POST)
    public AssetRead copyAsset(@PathVariable("sid") String sid,
                               @RequestParam(required = false) MultiValueMap<String, String> params) {
        AssetEntity assetEntity = assetService.copyByIdOrSystemRefId(sid);

        return assetConverter.fromEntityByRequestContext(assetEntity, QueryPreviewSizeConverter.getPreviewSizes(params));
    }
}
