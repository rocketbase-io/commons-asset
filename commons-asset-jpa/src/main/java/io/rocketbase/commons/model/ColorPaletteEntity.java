package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.ColorPalette;
import io.rocketbase.commons.model.converter.StringListConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.util.List;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ColorPaletteEntity {

    /**
     * dominant color
     */
    @Size(max = 7)
    @Column(name = "color_primary", length = 7)
    private String primary;

    /**
     * other colors ordered by priority (not containing primary)
     */
    @Size(max = 50)
    @Column(name = "color_others", length = 50)
    @Convert(converter = StringListConverter.class)
    private List<String> colors;

    @Transient
    public ColorPalette toApi() {
        return new ColorPalette(primary, colors);
    }
}
