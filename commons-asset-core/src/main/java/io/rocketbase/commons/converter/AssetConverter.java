package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.ApiProperties;
import io.rocketbase.commons.dto.asset.AssetMeta;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.model.AssetEntity;

import java.util.List;
import java.util.stream.Collectors;

public class AssetConverter extends AssetEssentialConverter {

    public AssetConverter(ApiProperties apiProperties, AssetPreviewService assetPreviewService) {
        super(apiProperties, assetPreviewService);
    }

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
                .context(entity.getContext())
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

        injectPreviewsAndDownload(result, sizes);

        return result;
    }

    public AssetReference fromEntityWithoutPreviews(AssetEntity entity) {
        if (entity == null) {
            return null;
        }

        return AssetReference.builder()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .context(entity.getContext())
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

}
