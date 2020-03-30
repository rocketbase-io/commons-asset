package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.converter.QueryAssetConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetUpdate;
import io.rocketbase.commons.dto.asset.AssetUploadMeta;
import io.rocketbase.commons.dto.asset.DefaultAssetUploadMeta;
import io.rocketbase.commons.exception.EmptyFileException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.AssetService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetBaseController implements BaseAssetController {

    @Resource
    private AssetRepository assetRepository;

    @Resource
    private AssetConverter assetConverter;

    @Resource
    private AssetService assetService;

    private QueryAssetConverter queryAssetConverter = new QueryAssetConverter();

    @SneakyThrows
    @RequestMapping(method = RequestMethod.POST)
    public AssetRead handleFileUpload(@RequestParam("file") MultipartFile file,
                                      @RequestParam(required = false) MultiValueMap<String, String> params) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }
        AssetEntity asset = assetService.store(file.getInputStream(), file.getOriginalFilename(), file.getSize(), null, convert(params));
        return assetConverter.fromEntityByRequestContext(asset, getPreviewSizes(null));
    }

    protected AssetUploadMeta convert(MultiValueMap<String, String> params) {
        DefaultAssetUploadMeta result = new DefaultAssetUploadMeta();
        Map<String, String> keyValues = new HashMap<>();
        for (String p : params.keySet()) {
            if (p.startsWith("k_") && p.length() > 2) {
                keyValues.put(p.substring(2), params.getFirst(p));
            }
        }
        result.setKeyValues(keyValues);
        result.setSystemRefId(params.getFirst("systemRefId"));
        result.setContext(params.getFirst("context"));
        result.setEol(parseInstant(params, "eol", null));
        return result;
    }

    @RequestMapping(method = RequestMethod.GET)
    public PageableResult<AssetRead> findAll(@RequestParam(required = false) MultiValueMap<String, String> params) {

        Page<AssetEntity> pageResult = assetRepository.findAll(queryAssetConverter.fromParams(params), parsePageRequest(params));

        return PageableResult.contentPage(assetConverter.fromEntities(pageResult.getContent(), getPreviewSizes(params)), pageResult);
    }

    @RequestMapping(value = "/{sid}", method = RequestMethod.GET)
    public AssetRead getAsset(@PathVariable("sid") String sid, @RequestParam(required = false) MultiValueMap<String, String> params) {
        AssetEntity entity = assetService.findByIdOrSystemRefId(sid)
                .orElseThrow(() -> new NotFoundException());
        return assetConverter.fromEntityByRequestContext(entity, getPreviewSizes(params));
    }

    @RequestMapping(value = "/{sid}", method = RequestMethod.PUT)
    public AssetRead updateAsset(@PathVariable("sid") String sid, @RequestBody @NotNull @Validated AssetUpdate update,
                                 @RequestParam(required = false) MultiValueMap<String, String> params) {
        AssetEntity entity = assetService.findByIdOrSystemRefId(sid)
                .orElseThrow(() -> new NotFoundException());
        return assetConverter.fromEntityByRequestContext(assetService.updateKeyValues(entity, update.getKeyValues()), getPreviewSizes(params));
    }


}
