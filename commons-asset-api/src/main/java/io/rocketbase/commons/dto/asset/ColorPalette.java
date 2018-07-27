package io.rocketbase.commons.dto.asset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColorPalette implements Serializable {

    /**
     * dominant color
     */
    private String primary;

    /**
     * other colors ordered by priority (not containing primary)
     */
    private List<String> colors;

}
