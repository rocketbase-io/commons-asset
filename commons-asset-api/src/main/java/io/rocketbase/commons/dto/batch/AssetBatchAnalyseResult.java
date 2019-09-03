package io.rocketbase.commons.dto.batch;

import io.rocketbase.commons.dto.asset.AssetAnalyse;
import io.rocketbase.commons.exception.AssetErrorCodes;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetBatchAnalyseResult implements Serializable {

    /**
     * key holds the given url
     */
    @Singular("success")
    private Map<String, AssetAnalyse> succeeded;

    /**
     * key holds the given url
     */
    @Singular("failure")
    private Map<String, AssetErrorCodes> failed;

}
