package io.rocketbase.commons.dto.batch;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssetBatchWrite implements Serializable {

    @Singular
    @NotNull
    private List<AssetBatchWriteEntry> entries;
}
