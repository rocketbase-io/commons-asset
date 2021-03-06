package io.rocketbase.commons.dto.asset;


import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import javax.annotation.Nullable;

import java.beans.Transient;
import java.io.Serializable;

/**
 * resolution of an image in pixels
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "resolution of an image in pixels")
public class Resolution implements Serializable {

    /**
     * width in pixel
     */
    @Schema(description = "width in pixel", example = "800")
    private Integer width;

    /**
     * height in pixel
     */
    @Schema(description = "height in pixel", example = "600")
    private Integer height;

    public Resolution(Resolution other) {
        this.width = other.width;
        this.height = other.height;
    }

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
     * check's if resolution is bigger than given size
     *
     * @return true = bigger then given parameter, false = smaller or null
     */
    public boolean isBiggerThan(int maxWidth, int maxHeight) {
        if (width == null || height == null) {
            return false;
        }
        return width > maxWidth || height > maxHeight;
    }

    /**
     * check's if resolution is bigger than given size
     *
     * @return true = bigger then given parameter, false = smaller or null
     */
    public boolean isBiggerThan(PreviewSize previewSize) {
        if (previewSize == null) {
            return false;
        }
        return isBiggerThan(previewSize.getMaxWidth(), previewSize.getMaxHeight());
    }

    /**
     * check's if resolution is bigger than given size
     *
     * @return true = bigger then given parameter, false = smaller or null
     */
    public boolean isBiggerThan(Resolution resolution) {
        if (resolution == null) {
            return false;
        }
        return isBiggerThan(resolution.getWidth(), resolution.getHeight());
    }

    /**
     * check's if resolution is bigger than given size or at least bigger then previewSize before
     *
     * @return true = thumb should be calculated, false = original file too small
     */
    public boolean shouldThumbBeCalculated(PreviewSize previewSize) {
        boolean result = isBiggerThan(previewSize);
        if (!result && previewSize != null && previewSize.ordinal() > 0 ) {
            result = isBiggerThan(PreviewSize.values()[previewSize.ordinal()-1]);
        }
        return result;
    }

    /**
     * calculate the final resolution in case the image should fit within given size
     *
     * @return null in case of missing width/height<br>
     * same resolution when it's not bigger as given size<br>
     * new resolution that fit's in given size
     */
    @Nullable
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

    @Override
    public String toString() {
        return String.format("%d x %d", width, height);
    }
}
