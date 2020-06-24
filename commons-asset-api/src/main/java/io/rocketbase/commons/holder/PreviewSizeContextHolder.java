package io.rocketbase.commons.holder;

import io.rocketbase.commons.converter.QueryPreviewSizeConverter;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.util.Nulls;
import org.springframework.util.MultiValueMap;

import java.util.List;

public final class PreviewSizeContextHolder {

    private static ThreadLocal<List<PreviewSize>> currentPreviewSizes = new ThreadLocal<>();

    /**
     * will never be null
     */
    public static List<PreviewSize> getCurrent() {
        return Nulls.notNull(currentPreviewSizes.get(), QueryPreviewSizeConverter.DEFAULT);
    }

    public static void setCurrent(List<PreviewSize> previewSizes) {
        currentPreviewSizes.set(previewSizes);
    }

    /**
     * when key previewSize exists in params updates context
     */
    public static void update(MultiValueMap<String, String> params) {
        update("previewSize", params);
    }

    /**
     * update context when key and params exists and not empty list
     */
    public static void update(String key, MultiValueMap<String, String> params) {
        List<PreviewSize> sizes = QueryPreviewSizeConverter.getPreviewSizes(key, params, null);
        if (sizes != null && !sizes.isEmpty()) {
            setCurrent(sizes);
        }
    }

}
