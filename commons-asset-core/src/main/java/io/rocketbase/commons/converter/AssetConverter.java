package io.rocketbase.commons.converter;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import io.rocketbase.commons.config.AssetApiProperties;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.holder.PreviewSizeContextHolder;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.util.Nulls;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;

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

                Pair<List<PreviewSize>, Boolean> detectedSizes = detectSizes(result, sizes);
                // fill previewUrls
                detectedSizes.getFirst()
                        .forEach(s -> assetPreviews.getPreviewMap()
                                .put(s, assetPreviewService.getPreviewUrl(result, s)));
                // calculate responsiveImage
                assetPreviews.setResponsive(calculateResponsive(result, detectedSizes.getFirst(), detectedSizes.getSecond()));

            }
            if (assetApiProperties.isDownload()) {
                result.setDownload(assetPreviewService.getDownloadUrl(result));
            }
        }
    }

    protected Pair<List<PreviewSize>, Boolean> detectSizes(AssetReference reference, List<PreviewSize> sizes) {
        // detect requested previewSizes
        List<PreviewSize> previewSizes = assetApiProperties.filterAllowedPreviewSizes(((sizes == null || sizes.isEmpty()) ? getDefaultPreviewSizes() : sizes));
        // filter too tall previewSizes (that are larger then original resolution)
        boolean skippedSizes = false;
        Resolution resolution = Nulls.notNull(reference.getMeta(), AssetMeta::getResolution, null);
        if (resolution != null) {
            List<PreviewSize> tooBigSizes = previewSizes.stream()
                    .filter(size -> !resolution.shouldThumbBeCalculated(size))
                    .collect(Collectors.toList());
            skippedSizes = !tooBigSizes.isEmpty();
            previewSizes.removeAll(tooBigSizes);

        }
        return Pair.of(previewSizes, skippedSizes);
    }

    protected ResponsiveImage calculateResponsive(AssetReference reference, List<PreviewSize> previewSizes, boolean skippedSizes) {
        Resolution resolution = Nulls.notNull(reference.getMeta(), AssetMeta::getResolution, null);
        if (!Nulls.noneNullValue(reference, previewSizes)) {
            return null;
        }
        ResponsiveImage result = new ResponsiveImage();
        if (resolution == null && assetApiProperties.isDownload()) {
            result.setSrc(assetPreviewService.getDownloadUrl(reference));
            return result;
        }
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
        return toRead(reference, getPreviewSizes());
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

    public AssetDisplay toDisplay(AssetReference reference) {
        return toDisplay(reference, getPreviewSizes());
    }

    public AssetDisplay toDisplay(AssetReference reference, List<PreviewSize> sizes) {
        if (reference == null) {
            return null;
        }
        Pair<List<PreviewSize>, Boolean> detectedSizes = detectSizes(reference, sizes);

        return AssetDisplay.builder()
                .id(reference.getId())
                .type(reference.getType())
                .meta(reference.getMeta())
                .image(calculateResponsive(reference, detectedSizes.getFirst(), detectedSizes.getSecond()))
                .download(assetApiProperties.isDownload() ? assetPreviewService.getDownloadUrl(reference) : null)
                .lqip(reference.getLqip())
                .build();
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
                .modifiedBy(entity.getModifiedBy())
                .modified(entity.getModified())
                .build();
    }

    public AssetReference fromEntityWithoutPreviews(AssetEntity entity) {
        if (entity == null) {
            return null;
        }
        return entity.toReference();
    }

    public List<AssetRead> fromEntities(List<? extends AssetEntity> entities, List<PreviewSize> sizes) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(v -> fromEntity(v, sizes)).collect(Collectors.toList());
    }

    @Nullable
    protected List<PreviewSize> getPreviewSizes() {
        try {
            return PreviewSizeContextHolder.hasValueSet() ? PreviewSizeContextHolder.getCurrent() : null;
        } catch (Exception e) {
            // could maybe throw error in case of reactive environment...
            return null;
        }
    }

}
