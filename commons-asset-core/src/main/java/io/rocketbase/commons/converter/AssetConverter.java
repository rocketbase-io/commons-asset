package io.rocketbase.commons.converter;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.holder.PreviewSizeContextHolder;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AssetConverter {

    public static final String RESPONSIVE_SIZES_FORMAT = "(max-width: {0,number,#}px) 100vw, {0,number,#}px";
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
                AssetPreviews assetPreviews = new AssetPreviews(new TreeMap<>(), null);
                result.setPreviews(assetPreviews);

                // detect requested previewSizes
                List<PreviewSize> previewSizes = assetApiProperties.filterAllowedPreviewSizes(((sizes == null || sizes.isEmpty()) ? getDefaultPreviewSizes() : sizes));
                // filter too tall previewSizes (that are larger then original resolution)
                boolean skippedSizes = false;
                Resolution resolution = result.getMeta() != null ? result.getMeta().getResolution() : null;
                if (resolution != null) {
                    List<PreviewSize> tooBigSizes = previewSizes.stream()
                            .filter(size -> !resolution.shouldThumbBeCalculated(size))
                            .collect(Collectors.toList());
                    skippedSizes = !tooBigSizes.isEmpty();
                    previewSizes.removeAll(tooBigSizes);

                }
                // fill previewUrls
                previewSizes
                        .forEach(s -> assetPreviews.getPreviewMap()
                                .put(s, assetPreviewService.getPreviewUrl(result, s)));
                // calculate responsiveImage
                assetPreviews.setResponsive(calculateResponsive(result, previewSizes, resolution, skippedSizes));

            }
            if (assetApiProperties.isDownload()) {
                result.setDownload(assetPreviewService.getDownloadUrl(result));
            }
        }
    }

    protected ResponsiveImage calculateResponsive(AssetReference reference, List<PreviewSize> previewSizes, Resolution resolution, boolean skippedSizes) {
        if (!Nulls.noneNullValue(reference, previewSizes, resolution)) {
            return null;
        }
        ResponsiveImage result = new ResponsiveImage();
        List<String> srcSet = new ArrayList<>();
        Resolution calculated = null;
        String previewUrl = null;
        for (PreviewSize size : Sets.newTreeSet(previewSizes)) {
            calculated = resolution.calculateWithAspectRatio(size.getMaxWidth(), size.getMaxHeight());
            previewUrl = assetPreviewService.getPreviewUrl(reference, size);
            srcSet.add(String.format("%s %dw", previewUrl, calculated.getWidth()));
        }
        if (skippedSizes && assetApiProperties.isDownload()) {
            String downloadUrl = assetPreviewService.getDownloadUrl(reference);
            srcSet.add(String.format("%s %dw", downloadUrl, resolution.getWidth()));
            result.setSrc(downloadUrl);
            result.setSizes(MessageFormat.format(RESPONSIVE_SIZES_FORMAT, resolution.getWidth()));
        } else {
            result.setSrc(previewUrl);
            result.setSizes(MessageFormat.format(RESPONSIVE_SIZES_FORMAT, calculated.getWidth()));
        }
        result.setSrcset(Joiner.on(", ").join(srcSet));
        return result;
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
        return entity.toReference();
    }

    public List<AssetRead> fromEntities(List<AssetEntity> entities, List<PreviewSize> sizes) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(v -> fromEntity(v, sizes)).collect(Collectors.toList());
    }

}
