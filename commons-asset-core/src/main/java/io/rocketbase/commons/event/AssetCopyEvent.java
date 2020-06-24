package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AssetEntity;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * event that is been triggered before data copied
 */
@Getter
public class AssetCopyEvent extends ApplicationEvent {

    private AssetEntity sourceEntity;
    private AssetEntity targetEntity;

    public AssetCopyEvent(Object source, AssetEntity sourceEntity, AssetEntity targetEntity) {
        super(source);
        this.sourceEntity = sourceEntity;
        this.targetEntity = targetEntity;
    }
}
