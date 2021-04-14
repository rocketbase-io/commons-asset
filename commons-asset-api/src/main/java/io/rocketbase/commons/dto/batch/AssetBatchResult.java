package io.rocketbase.commons.dto.batch;

import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.exception.AssetErrorCodes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetBatchResult implements Serializable {

    /**
     * key holds the given url
     */
    private Map<String, AssetRead> succeeded;

    /**
     * key holds the given url
     */
    private Map<String, AssetErrorCodes> failed;

    public void addSuccess(String url, AssetRead read) {
        if (succeeded == null) {
            succeeded = new LinkedHashMap<>();
        }
        succeeded.put(url, read);
    }

    public void addFailure(String url, AssetErrorCodes errorCodes) {
        if (failed == null) {
            failed = new LinkedHashMap<>();
        }
        failed.put(url, errorCodes);
    }

}
