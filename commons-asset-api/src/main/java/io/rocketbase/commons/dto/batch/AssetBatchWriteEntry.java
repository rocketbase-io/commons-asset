package io.rocketbase.commons.dto.batch;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;


@Data
@AllArgsConstructor
public class AssetBatchWriteEntry implements Serializable {

    @NotNull
    private String url;

    private String systemRefId;

    private String context;

    private Map<String, String> keyValues;

    public AssetBatchWriteEntry() {
    }

    public AssetBatchWriteEntry(String url) {
        this.url = url;
    }

}
