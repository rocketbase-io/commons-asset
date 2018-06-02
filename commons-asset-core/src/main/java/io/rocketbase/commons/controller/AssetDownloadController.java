package io.rocketbase.commons.controller;

import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@ConditionalOnExpression(value = "${asset.api.download:true}")
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetDownloadController implements BaseAssetController {

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetRepository assetRepository;

    /**
     * used to get raw content
     *
     * @param sid id or systemRefId of asset
     * @return content-stream
     */
    @RequestMapping(value = "/{sid}/b", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadAsset(@PathVariable("sid") String sid) {
        AssetEntity entity = assetRepository.getByIdOrSystemRefId(sid);
        InputStreamResource streamResource = fileStorageService.download(entity);

        return ResponseEntity.ok()
                .contentLength(entity.getFileSize())
                .contentType(MediaType.parseMediaType(entity.getType().getContentType()))
                .body(streamResource);
    }


}
