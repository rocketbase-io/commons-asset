package io.rocketbase.commons.dto.batch;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(onConstructor = @_(@JsonCreator))
public class AssetBatchWrite implements Serializable {

    @Singular
    @NotNull
    private List<AssetBatchWriteEntry> entries;
}
