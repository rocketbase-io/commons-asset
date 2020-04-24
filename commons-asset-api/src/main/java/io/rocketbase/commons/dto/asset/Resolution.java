package io.rocketbase.commons.dto.asset;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
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

    /**
     * check's if resolution if bigger than given size
     *
     * @return true = bigger then given parameter, false = smaller or null
     */
    @JsonIgnore
    public boolean isBiggerThan(int maxWidth, int maxHeight) {
        if (width == null || height == null) {
            return false;
        }
        return width > maxWidth || height > maxHeight;
    }

    /**
     * calculate the final resolution in case the image should fit within given size
     *
     * @return null in case of missing width/height<br>
     * same resolution when it's not bigger as given size<br>
     * new resolution that fit's in given size
     */
    @Nullable
    @JsonIgnore
    public Resolution calculateWithAspectRatio(int maxWidth, int maxHeight) {
        if (width == null || height == null) {
            return null;
        }
        if (isBiggerThan(maxWidth, maxHeight)) {
            double widthRatio = width / (double) maxWidth;
            double heightRatio = height / (double) maxHeight;
            if (heightRatio > widthRatio) {
                return new Resolution((int) Math.round(width / heightRatio), maxHeight);
            } else {
                return new Resolution(maxWidth, (int) Math.round(height / widthRatio));
            }
        } else {
            return this;
        }
    }
}
