package io.rocketbase.commons.dto.batch;

import io.rocketbase.commons.dto.asset.AssetReference;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * wrapped batch results for store service without previews
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "wrapped batch results for store service without previews")
public class AssetBatchResultWithoutPreviews implements Serializable {

    /**
     * key holds the given url
     */
    @Schema(description = "succeeded processing: key holds the given url. value the result.")
    private Map<String, AssetReference> succeeded;

    /**
     * key holds the given url
     */
    @Schema(description = "failed processing: key holds the given url. value the result.")
    private Map<String, AssetErrorCodes> failed;

    public void addSuccess(String url, AssetReference read) {
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
