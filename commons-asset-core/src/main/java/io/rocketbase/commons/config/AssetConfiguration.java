package io.rocketbase.commons.config;

import io.rocketbase.commons.dto.asset.AssetType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "asset")
public class AssetConfiguration {

    private String apiEndpoint = "/api/asset";

    private String renderEndpoint = "/get/asset";

    private String thumborHost = "http://localhost";

    private String thumborKey = "";

    private List<String> allowedTypes = new ArrayList<>();

    public List<AssetType> getAllowedAssetTypes() {
        List<AssetType> allowed = new ArrayList<>(EnumSet.allOf(AssetType.class));
        if (allowedTypes != null && !allowedTypes.isEmpty() && !allowedTypes.equals(Arrays.asList(""))) {
            allowed = allowedTypes.stream()
                    .map(v -> AssetType.valueOf(v.toUpperCase()))
                    .collect(Collectors.toList());
        }
        return allowed;
    }


}
