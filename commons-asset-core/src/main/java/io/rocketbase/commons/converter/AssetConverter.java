package io.rocketbase.commons.converter;

import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.AssetPreviews;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.holder.PreviewSizeContextHolder;
import io.rocketbase.commons.model.AssetEntity;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AssetConverter {

    private final AssetApiProperties assetApiProperties;
    private final AssetPreviewService assetPreviewService;

    public static Map<String, String> filterInvisibleKeys(Map<String, String> keyValues) {
        if (keyValues == null) {
            return null;
        }
        Map<String, String> map = new HashMap<>();
        keyValues.entrySet().stream()
                .filter(e -> !e.getKey().startsWith("_"))
                .forEach(e -> map.put(e.getKey(), e.getValue()));
        return map;
    }

    /**
     * you can overwrite this default to change your way of asset-conversion
     */
    public List<PreviewSize> getDefaultPreviewSizes() {
        return Arrays.asList(PreviewSize.S, PreviewSize.M, PreviewSize.L);
    }

    protected void injectPreviewsAndDownload(AssetRead result, List<PreviewSize> sizes) {
        if (result != null) {
            if (result.getType() != null && assetPreviewService.isPreviewSupported(result.getType())) {
                result.setPreviews(AssetPreviews.builder()
                        .previewMap(new HashMap<>())
                        .build());

                assetApiProperties.filterAllowedPreviewSizes(((sizes == null || sizes.isEmpty()) ? getDefaultPreviewSizes() : sizes))
                        .forEach(s -> result.getPreviews().getPreviewMap()
                                .put(s, assetPreviewService.getPreviewUrl(result, s)));
            }
            if (assetApiProperties.isDownload()) {
                result.setDownload(assetPreviewService.getDownloadUrl(result));
            }
        }
    }

    public AssetRead toRead(AssetReference reference) {
        List<PreviewSize> sizes = null;
        try {
            sizes = PreviewSizeContextHolder.hasValueSet() ? PreviewSizeContextHolder.getCurrent() : null;
        } catch (Exception e) {
            // could maybe throw error in case of reactive environment...
        }
        return toRead(reference, sizes);
    }

    public AssetRead toRead(AssetReference reference, List<PreviewSize> sizes) {
        return toRead(reference, sizes, null, null);
    }

    public AssetRead toRead(AssetReference reference, List<PreviewSize> sizes, Map<String, String> keyValues, Instant eol) {
        if (reference == null) {
            return null;
        }

        AssetRead result = AssetRead.builderRead()
                .id(reference.getId())
                .systemRefId(reference.getSystemRefId())
                .context(reference.getContext())
                .urlPath(reference.getUrlPath())
                .type(reference.getType())
                .meta(reference.getMeta())
                .keyValues(filterInvisibleKeys(keyValues))
                .lqip(reference.getLqip())
                .eol(eol)
                .build();

        injectPreviewsAndDownload(result, sizes);

        return result;
    }

    /**
     * extract baseUrl from RequestContextHolder - so in case there is no RequestContext you get errors!
     */
    public AssetRead fromEntityByRequestContext(AssetEntity entity, List<PreviewSize> sizes) {
        return fromEntity(entity, sizes);
    }

    public AssetRead fromEntity(AssetEntity entity, List<PreviewSize> sizes) {
        AssetRead result = fromEntity(entity);
        injectPreviewsAndDownload(result, sizes);
        return result;
    }

    public AssetRead fromEntity(AssetEntity entity) {
        if (entity == null) {
            return null;
        }
        return AssetRead.builderRead()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .context(entity.getContext())
                .urlPath(entity.getUrlPath())
                .type(entity.getType())
                .meta(entity.getMeta())
                .keyValues(filterInvisibleKeys(entity.getKeyValues()))
                .lqip(entity.getLqip())
                .eol(entity.getEol())
                .build();
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
                .meta(entity.getMeta())
                .lqip(entity.getLqip())
                .build();
    }

    public List<AssetRead> fromEntities(List<AssetEntity> entities, List<PreviewSize> sizes) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(v -> fromEntity(v, sizes)).collect(Collectors.toList());
    }

}
