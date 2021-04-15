package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Blob;


@Entity
@Table(name = "co_asset_file")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetFileEntity {

    /**
     * longer to keep different preview sizes
     */
    @Id
    @Column(name = "id", length = 39)
    private String id;

    @Lob
    @NotNull
    @Column(name = "file_binary", nullable = false)
    private Blob binary;
}
