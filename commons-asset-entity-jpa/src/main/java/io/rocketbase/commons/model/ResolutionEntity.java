package io.rocketbase.commons.model;

import io.rocketbase.commons.dto.asset.Resolution;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResolutionEntity {

    @Column(name = "resolution_width")
    private Integer width;

    @Column(name = "resolution_height")
    private Integer height;

    @Transient
    public Resolution toApi() {
        return new Resolution(width, height);
    }
}
