package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AssetEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * event that is been triggered before updates to meta are stored within database
 */
@Getter
public class AssetUpdateMetaEvent extends ApplicationEvent {

    private AssetEntity assetEntity;

    public AssetUpdateMetaEvent(Object source, AssetEntity assetEntity) {
        super(source);
        this.assetEntity = assetEntity;
    }
}
