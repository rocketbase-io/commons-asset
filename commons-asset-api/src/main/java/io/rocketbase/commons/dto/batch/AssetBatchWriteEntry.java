package io.rocketbase.commons.dto.batch;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Data
@AllArgsConstructor
public class AssetBatchWriteEntry implements Serializable {

    @NotNull
    @URL
    private String url;

    private String systemRefId;

    private String context;

    public AssetBatchWriteEntry() {
    }

    public AssetBatchWriteEntry(String url) {
        this.url = url;
    }

}
