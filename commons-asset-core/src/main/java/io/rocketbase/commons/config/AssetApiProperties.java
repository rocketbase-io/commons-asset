package io.rocketbase.commons.config;


import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.PreviewSize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.*;

@Data
@ConfigurationProperties(prefix = "asset.api")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetApiProperties implements Serializable {

    private String baseUrl = "";

    private String path = "/api/asset";

    private List<AssetType> types = new ArrayList<>(EnumSet.allOf(AssetType.class));

    /**
     * you can limit the allowed previewSizes
     */
    private List<PreviewSize> previewSizes = new ArrayList<>(EnumSet.allOf(PreviewSize.class));

    private boolean precalculate = false;

    private boolean download = true;

    private boolean delete = true;

    private boolean copy = true;

    private boolean batch = true;

    private boolean preview = true;

    private boolean detectResolution = true;

    private boolean detectColors = true;

    private boolean analyse = true;

    /**
     * in case filestorage is not present - what option should be used for preview generation
     */
    private boolean localEndpointFallback = true;

    private Map<String, String> downloadHeaders = new HashMap<>();

    private Map<String, Float> previewQuality = new HashMap<>();

    public List<PreviewSize> filterAllowedPreviewSizes(List<PreviewSize> sizes) {
        List<PreviewSize> result = new ArrayList<>(previewSizes);
        result.retainAll(sizes);
        return result;
    }

}
