package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AssetEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * event that is been triggered before data is stored within database<br>
 * could be used for example to audit uploaded user in key-value pairs
 */
@Getter
public class AssetUploadEvent extends ApplicationEvent {

    private AssetEntity assetEntity;

    public AssetUploadEvent(Object source, AssetEntity assetEntity) {
        super(source);
        this.assetEntity = assetEntity;
    }
}
