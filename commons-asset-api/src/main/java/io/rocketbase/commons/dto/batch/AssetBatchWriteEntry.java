package io.rocketbase.commons.dto.batch;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
public class AssetBatchWriteEntry {

    @NotNull
    @URL
    private String url;

    private String systemRefId;

    public AssetBatchWriteEntry() {
    }

    public AssetBatchWriteEntry(String url) {
        this.url = url;
    }

}
