package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.ApiProperties;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.model.AssetEntity;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AssetConverter {

    private final ApiProperties apiProperties;
    private final AssetPreviewService assetPreviewService;

    private List<PreviewSize> defaultSizes = Arrays.asList(PreviewSize.S, PreviewSize.M, PreviewSize.L);

    /**
     * extract baseUrl from RequestContextHolder - so in case there is no RequestContext you get errors!
     */
    public AssetRead fromEntityByRequestContext(AssetEntity entity, List<PreviewSize> sizes) {
        return fromEntity(entity, sizes);
    }

    public AssetRead fromEntity(AssetEntity entity, List<PreviewSize> sizes) {
        if (entity == null) {
            return null;
        }
        AssetRead result = AssetRead.builderRead()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .urlPath(entity.getUrlPath())
                .type(entity.getType())
                .meta(AssetMeta.builder()
                        .created(entity.getCreated())
                        .fileSize(entity.getFileSize())
                        .originalFilename(entity.getOriginalFilename())
                        .resolution(entity.getResolution())
                        .colorPalette(entity.getColorPalette())
                        .referenceUrl(entity.getReferenceUrl())
                        .build())
                .build();

        result.setPreviews(AssetPreviews.builder()
                .previewMap(new HashMap<>())
                .build());

        if (entity.getType() != null && entity.getType().isImage()) {
            ((sizes == null || sizes.isEmpty()) ? defaultSizes : sizes)
                    .forEach(s -> result.getPreviews().getPreviewMap()
                            .put(s, assetPreviewService.getPreviewUrl(entity.getId(), entity.getUrlPath(), s)));
        }
        if (apiProperties.isDownload()) {
            result.setDownload(apiProperties.getPath() + "/" + entity.getId() + "/b");
        }

        return result;
    }

    public AssetReference fromEntityWithoutPreviews(AssetEntity entity) {
        if (entity == null) {
            return null;
        }

        return AssetReference.builder()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .urlPath(entity.getUrlPath())
                .type(entity.getType())
                .meta(AssetMeta.builder()
                        .created(entity.getCreated())
                        .fileSize(entity.getFileSize())
                        .originalFilename(entity.getOriginalFilename())
                        .resolution(entity.getResolution())
                        .colorPalette(entity.getColorPalette())
                        .referenceUrl(entity.getReferenceUrl())
                        .build())
                .build();
    }

    public List<AssetRead> fromEntities(List<AssetEntity> entities, List<PreviewSize> sizes) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(v -> fromEntity(v, sizes)).collect(Collectors.toList());
    }

    public AssetRead toRead(AssetReference reference) {
        return toRead(reference, null);
    }

    public AssetRead toRead(AssetReference reference, List<PreviewSize> sizes) {
        if (reference == null) {
            return null;
        }
        AssetPreviews assetPreviews = AssetPreviews.builder()
                .previewMap(new HashMap<>())
                .build();

        ((sizes == null || sizes.isEmpty()) ? defaultSizes : sizes)
                .forEach(s -> assetPreviews.getPreviewMap()
                        .put(s, assetPreviewService.getPreviewUrl(reference.getId(), reference.getUrlPath(), s)));

        return AssetRead.builderRead()
                .id(reference.getId())
                .systemRefId(reference.getSystemRefId())
                .urlPath(reference.getUrlPath())
                .type(reference.getType())
                .meta(reference.getMeta())
                .previews(assetPreviews)
                .build();
    }

}
