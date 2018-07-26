package io.rocketbase.commons.dto.batch;

import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.exception.AssetErrorCodes;
import lombok.*;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetBatchResult implements Serializable {

    /**
     * key holds the given url
     */
    @Singular("success")
    private Map<String, AssetRead> succeeded;

    /**
     * key holds the given url
     */
    @Singular("failure")
    private Map<String, AssetErrorCodes> failed;

}
