package io.rocketbase.commons.event;

import io.rocketbase.commons.dto.asset.AssetAnalyse;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.Nullable;

/**
 * event that is been triggered within asset batch saving and asset batch analyse
 */
@Getter
public class AssetUrlAnalysed extends ApplicationEvent {

    /**
     * analysed url
     */
    private String url;
    /**
     * analyse result
     */
    private AssetAnalyse analyse;

    /**
     * is only filled in case of batch processing within save/keeep function
     */
    @Nullable
    private String assetId;

    public AssetUrlAnalysed(Object source, String url, AssetAnalyse analyse, String assetId) {
        super(source);
        this.url = url;
        this.analyse = analyse;
        this.assetId = assetId;
    }
}
