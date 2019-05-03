package io.rocketbase.commons.config;


import io.rocketbase.commons.dto.asset.AssetType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.*;

@Data
@ConfigurationProperties(prefix = "asset.api")
public class ApiProperties implements Serializable {

    private String baseUrl = "";

    private String path = "/api/asset";

    private List<AssetType> types = new ArrayList<>(EnumSet.allOf(AssetType.class));

    private boolean download = true;

    private boolean delete = true;

    private boolean batch = true;

    private boolean preview = true;

    private boolean detectResolution = true;

    private boolean detectColors = true;

    private Map<String, String> downloadHeaders = new HashMap<>();

}
