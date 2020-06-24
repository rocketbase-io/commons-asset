package io.rocketbase.commons.converter;

import com.google.common.base.Splitter;
import io.rocketbase.commons.dto.asset.PreviewSize;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.*;

public class QueryPreviewSizeConverter {

    public static final List<PreviewSize> DEFAULT = Arrays.asList(PreviewSize.S, PreviewSize.M, PreviewSize.L);

    public static List<PreviewSize> getPreviewSizes(MultiValueMap<String, String> params) {
        return getPreviewSizes("size", params, DEFAULT);
    }

    public static List<PreviewSize> getPreviewSizes(String key, MultiValueMap<String, String> params, List<PreviewSize> fallback) {
        if (key != null && params != null && params.containsKey(key)) {
            Set<PreviewSize> previewSizes = new TreeSet<>();
            for (String val : params.get(key)) {
                if (!StringUtils.isEmpty(val) && val.contains(",")) {
                    Splitter.on(",").trimResults().omitEmptyStrings().split(val).forEach(v ->
                            previewSizes.add(PreviewSize.getByName(v, PreviewSize.S))
                    );
                }
                previewSizes.add(PreviewSize.getByName(val, PreviewSize.S));
            }
            return previewSizes.isEmpty() ? fallback : new ArrayList<>(previewSizes);
        } else {
            return fallback;
        }
    }
}
