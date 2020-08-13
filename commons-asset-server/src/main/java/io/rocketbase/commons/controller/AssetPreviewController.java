package io.rocketbase.commons.controller;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.exception.NotFoundException;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.AssetService;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.handler.AssetHandler;
import io.rocketbase.commons.service.handler.PreviewConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.TimeUnit;

@RestController
@ConditionalOnExpression(value = "${asset.api.preview:true}")
@RequestMapping("${asset.api:/api/asset}")
@RequiredArgsConstructor
@Slf4j
public class AssetPreviewController implements BaseController {

    private final AssetApiProperties assetApiProperties;
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

        if (assetApiProperties.isPrecalculate()) {
            if (fileStorageService.useDownloadPreviewUrl()) {
                HttpHeaders headers = new HttpHeaders();
                headers.add("Location", fileStorageService.getDownloadPreviewUrl(entity, previewSize));
                return new ResponseEntity<>(headers, HttpStatus.FOUND);
            } else {
                InputStreamResource streamResource = fileStorageService.downloadPreview(entity, previewSize);
                File previewDownload = null;
                try {
                    previewDownload = File.createTempFile("asset-preview", entity.getType().getFileExtensionForSuffix());
                    IOUtils.copy(streamResource.getInputStream(), new FileOutputStream(previewDownload));

                    FileSystemResource resource = new FileSystemResource(previewDownload);
                    return buildResponseEntity(streamResource, entity, previewSize);

                } finally {
                    if (previewDownload != null) {
                        previewDownload.delete();
                    }
                }
            }
        }

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
            return buildResponseEntity(resource, entity, previewSize);
        } finally {
            if (download != null) {
                download.delete();
            }
        }
    }

    @SneakyThrows
    protected ResponseEntity buildResponseEntity(AbstractResource resource, AssetEntity entity, PreviewSize previewSize) {
        return ResponseEntity.ok()
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(entity.getType().getContentType()))
                .eTag(String.format("%s-%s", entity.getId(), previewSize.name().toLowerCase()))
                .cacheControl(CacheControl.maxAge(30, TimeUnit.DAYS))
                .body(resource);
    }


}
