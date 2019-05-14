package io.rocketbase.commons.controller;

import io.rocketbase.commons.converter.AssetConverter;
import io.rocketbase.commons.converter.QueryAssetConverter;
import io.rocketbase.commons.dto.PageableResult;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.exception.EmptyFileException;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.AssetService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

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
                                      @RequestParam(value = "systemRefId", required = false) String systemRefId) {
        if (file.isEmpty()) {
            throw new EmptyFileException();
        }

        AssetEntity asset = assetService.store(file.getInputStream(), file.getOriginalFilename(), file.getSize(), systemRefId);


        return assetConverter.fromEntityByRequestContext(asset, getPreviewSizes(null));
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


}
