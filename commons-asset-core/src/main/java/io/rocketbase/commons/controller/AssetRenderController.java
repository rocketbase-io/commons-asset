package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetRepository;
import io.rocketbase.commons.service.FileStorageService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
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
@RequestMapping("${asset.api.render:/get/asset}")
@Slf4j
public class AssetRenderController implements BaseController {

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetRepository assetRepository;

    @RequestMapping(value = "/{sid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getOriginal(@PathVariable("sid") String sid) {
        AssetEntity entity = assetRepository.getByIdOrSystemRefId(sid);
        InputStreamResource streamResource = fileStorageService.download(entity);

        return ResponseEntity.ok()
                .contentLength(entity.getFileSize())
                .contentType(MediaType.parseMediaType(entity.getType().getContentType()))
                .body(streamResource);
    }

    @SneakyThrows
    @RequestMapping(value = "/{sid}/{size}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<InputStreamResource> getPreview(@PathVariable("sid") String sid, @PathVariable("size") String size) {
        PreviewSize previewSize = PreviewSize.getByName(size, PreviewSize.S);

        AssetEntity entity = assetRepository.getByIdOrSystemRefId(sid);
        if (!entity.getType().isImage()) {
            throw new NotFoundException();
        }

        InputStreamResource streamResource = fileStorageService.download(entity);

        ByteArrayOutputStream thumbOs = new ByteArrayOutputStream();
        Thumbnails.of(streamResource.getInputStream())
                .size(previewSize.getMaxWidth(), previewSize.getMaxHeight())
                .toOutputStream(thumbOs);

        return ResponseEntity.ok()
                .contentLength(thumbOs.toByteArray().length)
                .contentType(MediaType.parseMediaType(entity.getType().getContentType()))
                .eTag(String.format("%s-%s", entity.getId(), previewSize.name().toLowerCase()))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .body(new InputStreamResource(new ByteArrayInputStream(thumbOs.toByteArray())));
    }
}
