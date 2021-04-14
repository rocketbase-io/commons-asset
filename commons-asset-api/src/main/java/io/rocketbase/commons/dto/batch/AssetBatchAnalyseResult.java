package io.rocketbase.commons.dto.batch;

import io.rocketbase.commons.dto.asset.AssetAnalyse;
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
public class AssetBatchAnalyseResult implements Serializable {

    /**
     * key holds the given url
     */
    private Map<String, AssetAnalyse> succeeded;

    /**
     * key holds the given url
     */
    private Map<String, AssetErrorCodes> failed;

    public void addSuccess(String url, AssetAnalyse analyse) {
        if (succeeded == null) {
            succeeded = new LinkedHashMap<>();
        }
        succeeded.put(url, analyse);
    }

    public void addFailure(String url, AssetErrorCodes errorCodes) {
        if (failed == null) {
            failed = new LinkedHashMap<>();
        }
        failed.put(url, errorCodes);
    }

}
