package io.rocketbase.commons.controller;

import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetService;
import io.rocketbase.commons.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
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
    private AssetService assetService;

    /**
     * used to get raw content
     *
     * @param sid id or systemRefId of asset
     * @return content-stream
     */
    @RequestMapping(value = "/{sid}/b", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> downloadAsset(@PathVariable("sid") String sid, @RequestParam(required = false) MultiValueMap<String, String> params) {
        AssetEntity entity = assetService.findByIdOrSystemRefId(sid)
                .orElseThrow(() -> new NotFoundException());
        InputStreamResource streamResource = fileStorageService.download(entity);

        ResponseEntity.BodyBuilder builder = ResponseEntity.ok()
                .contentLength(entity.getFileSize());
        if (params.containsKey("inline")) {
            builder.header(HttpHeaders.CONTENT_DISPOSITION, "inline");
        } else {
            builder.header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment;filename=%s", entity.getOriginalFilename()));
        }
        return builder
                .contentType(MediaType.parseMediaType(entity.getType().getContentType()))
                .body(streamResource);
    }


}
