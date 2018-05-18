package io.rocketbase.commons.dto.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.rocketbase.commons.dto.asset.AssetRead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor(onConstructor = @_(@JsonCreator))
public class AssetBatchResult implements Serializable {

    @Singular("resultEntry")
    private Map<String, AssetRead> result;

}
