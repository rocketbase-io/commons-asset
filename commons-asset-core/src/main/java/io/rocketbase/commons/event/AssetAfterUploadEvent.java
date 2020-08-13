package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.OriginalUploadModifier;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * event that is been triggered after data is stored within database<br>
 * could be used for example to calculate previews...
 */
@Getter
public class AssetAfterUploadEvent extends ApplicationEvent {

    private AssetEntity assetEntity;
    private OriginalUploadModifier.Modification modification;

    public AssetAfterUploadEvent(Object source, AssetEntity assetEntity, OriginalUploadModifier.Modification modification) {
        super(source);
        this.assetEntity = assetEntity;
        this.modification = modification;
    }
}
