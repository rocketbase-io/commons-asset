package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetService;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.ImagePreviewRendering;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

@RestController
@ConditionalOnExpression(value = "${asset.api.preview:true}")
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetPreviewController implements BaseAssetController {

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetService assetService;

    @Resource
    private ImagePreviewRendering imagePreviewRendering;

    @SneakyThrows
    @RequestMapping(value = "/{sid}/{size}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getPreview(@PathVariable("sid") String sid, @PathVariable("size") String size) {
        PreviewSize previewSize = PreviewSize.getByName(size, PreviewSize.S);
        AssetEntity entity = assetService.findByIdOrSystemRefId(sid)
                .orElseThrow(() -> new NotFoundException());

        if (!entity.getType().isImage()) {
            throw new NotFoundException();
        }

        InputStreamResource streamResource = fileStorageService.download(entity);

        ByteArrayOutputStream thumbOs = imagePreviewRendering.getPreview(entity.getType(), streamResource.getInputStream(), previewSize);

        return ResponseEntity.ok()
                .contentLength(thumbOs.toByteArray().length)
                .contentType(MediaType.parseMediaType(entity.getType().getContentType()))
                .eTag(String.format("%s-%s", entity.getId(), previewSize.name().toLowerCase()))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .body(new InputStreamResource(new ByteArrayInputStream(thumbOs.toByteArray())));
    }


}
