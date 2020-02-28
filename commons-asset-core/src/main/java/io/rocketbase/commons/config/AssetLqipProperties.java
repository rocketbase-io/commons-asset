package io.rocketbase.commons.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "asset.lqip")
/**
 * Low Quality Image Placeholder (placeholder for images in base64 encoded - ultra low res)<br>
 *     could be part of the AssetReference in order to display this version without any extra http request
 */
public class AssetLqipProperties implements Serializable {

    private boolean enabled = false;

    private int maxWidth = 50;
    private int maxHeight = 50;
    private float quality = 0.05f;

}
