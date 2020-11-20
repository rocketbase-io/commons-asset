package io.rocketbase.commons.model.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetJpaKeyValueId implements Serializable {

    private String asset;
    private String fieldKey;

}
