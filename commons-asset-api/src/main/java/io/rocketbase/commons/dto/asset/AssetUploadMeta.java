package io.rocketbase.commons.dto.asset;

import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public interface AssetUploadMeta extends Serializable {

    /**
     * optional - reference id (needs to be unique within system)
     */
    @Nullable
    String getSystemRefId();

    /**
     * optional - name of context (could be used to differ buckets for example)
     */
    @Nullable
    String getContext();

    /**
     * optional - will get stored with lowercase<br>
     * max length of 50 characters<br>
     * key with _ as prefix will not get displayed in REST_API
     */
    @Nullable
    Map<String, String> getKeyValues();

    /**
     * optional - after this time the asset could get deleted within a cleanup job
     */
    @Nullable
    Instant getEol();
}
