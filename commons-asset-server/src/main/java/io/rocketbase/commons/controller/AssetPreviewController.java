package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetService;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.handler.AssetHandler;
import io.rocketbase.commons.service.handler.PreviewConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

@RestController
@ConditionalOnExpression(value = "${asset.api.preview:true}")
@RequestMapping("${asset.api:/api/asset}")
@Slf4j
public class AssetPreviewController implements BaseController {

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private AssetService assetService;

    @Resource
    private AssetHandler assetHandler;

    @SneakyThrows
    @RequestMapping(value = "/{sid}/{size}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<FileSystemResource> getPreview(@PathVariable("sid") String sid, @PathVariable("size") String size, @RequestParam(required = false) MultiValueMap<String, String> params) {
        PreviewSize previewSize = PreviewSize.getByName(size, PreviewSize.S);
        AssetEntity entity = assetService.findByIdOrSystemRefId(sid)
                .orElseThrow(() -> new NotFoundException());

        if (!assetHandler.isPreviewSupported(entity.getType())) {
            throw new NotFoundException();
        }

        InputStreamResource streamResource = fileStorageService.download(entity);

        File download = null;
        try {
            download = File.createTempFile("asset-download", entity.getType().getFileExtensionForSuffix());
            IOUtils.copy(streamResource.getInputStream(), new FileOutputStream(download));

            File preview = assetHandler.getPreview(entity.getType(), download, PreviewConfig.builder()
                    .previewSize(previewSize)
                    .rotation(parseInteger(params, "rotation", null))
                    .bg(params.getFirst("bg"))
                    .build());

            FileSystemResource resource = new FileSystemResource(preview);
            return ResponseEntity.ok()
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType(entity.getType().getContentType()))
                    .eTag(String.format("%s-%s", entity.getId(), previewSize.name().toLowerCase()))
                    .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                    .body(resource);
        } finally {
            if (download != null) {
                download.delete();
            }
        }
    }


}
