package io.rocketbase.commons.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * null properties mean let value as it is
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetUpdate implements Serializable {
    /**
     * will removed key that have value of null <br>
     * will only add/replace new/existing key values<br>
     * not mentioned key will still stay the same
     */
    @Nullable
    private Map<String, String> keyValues;

    /**
     * after this time the asset could get deleted within a cleanup job
     */
    @Nullable
    private Instant eol;

    public AssetUpdate(@Nullable Map<String, String> keyValues) {
        this.keyValues = keyValues;
    }

    public AssetUpdate(@Nullable Instant eol) {
        this.eol = eol;
    }
}
