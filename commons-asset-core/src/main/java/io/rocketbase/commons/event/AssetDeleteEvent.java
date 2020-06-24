package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AssetEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * event that is been triggered before data is getting deleted in database<br>
 * could be used for auditing for example
 */
@Getter
public class AssetDeleteEvent extends ApplicationEvent {

    private AssetEntity assetEntity;

    public AssetDeleteEvent(Object source, AssetEntity assetEntity) {
        super(source);
        this.assetEntity = assetEntity;
    }
}
