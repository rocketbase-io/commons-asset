package io.rocketbase.commons.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Blob;


@Entity
@Table(name = "asset_file")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssetFileEntity {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Lob
    @NotNull
    @Column(name = "file_binary", nullable = false)
    private Blob binary;
}