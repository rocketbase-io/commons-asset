package io.rocketbase.commons.event;

import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.OriginalUploadModifier;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * event that is been triggered before data is stored within database<br>
 * could be used for example to audit uploaded user in key-value pairs
 */
@Getter
public class AssetUploadEvent extends ApplicationEvent {

    private AssetEntity assetEntity;
    private OriginalUploadModifier.Modification modification;

    public AssetUploadEvent(Object source, AssetEntity assetEntity, OriginalUploadModifier.Modification modification) {
        super(source);
        this.assetEntity = assetEntity;
        this.modification = modification;
    }
}
