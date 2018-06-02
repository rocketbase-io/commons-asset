package io.rocketbase.commons.controller;

import io.rocketbase.commons.dto.asset.PreviewSize;
import org.springframework.util.MultiValueMap;

import java.util.*;

public interface BaseAssetController extends BaseController {

    default List<PreviewSize> getPreviewSizes(MultiValueMap<String, String> params) {
        if (params != null && params.containsKey("size")) {
            Set<PreviewSize> previewSizes = new TreeSet<>();
            for (String val : params.get("size")) {
                previewSizes.add(PreviewSize.getByName(val, PreviewSize.S));
            }
            return new ArrayList<>(previewSizes);
        } else {
            return Arrays.asList(PreviewSize.S, PreviewSize.M, PreviewSize.L);
        }
    }
}
