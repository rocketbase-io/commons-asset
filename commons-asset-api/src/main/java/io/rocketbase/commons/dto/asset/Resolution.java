package io.rocketbase.commons.dto.asset;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resolution implements Serializable {

    private Integer width;

    private Integer height;

    /**
     * calculates aspect ratio
     *
     * @return height : width <br>
     * in case of null return -1
     */
    @JsonIgnore
    public float getAspectRatio() {
        if (width != null && height != null && height > 0) {
            return width / (float) height;
        }
        return -1;
    }

    /**
     * is asset in landscape mode or portrait
     *
     * @return true = landscape, false = portrait
     */
    @JsonIgnore
    public boolean isLandscape() {
        return getAspectRatio() > 1.0f ? true : false;
    }
}
