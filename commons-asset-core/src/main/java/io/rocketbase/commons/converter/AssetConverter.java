package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.AssetMeta;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.model.AssetEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetConverter {

    public AssetRead fromEntity(AssetEntity entity, List<PreviewSize> sizes) {
        if (entity == null) {
            return null;
        }
        return AssetRead.builderRead()
                .id(entity.getId())
                .systemRefId(entity.getSystemRefId())
                .urlPath(entity.getUrlPath())
                .type(entity.getType())
                .meta(AssetMeta.builder()
                        .created(entity.getCreated())
                        .fileSize(entity.getFileSize())
                        .originalFilename(entity.getOriginalFilename())
                        .resolution(entity.getResolution())
                        .referenceUrl(entity.getReferenceUrl())
                        .build())
                .previews(null)
                .build();
    }

    public List<AssetRead> fromEntities(List<AssetEntity> entities, List<PreviewSize> sizes) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(v -> fromEntity(v, sizes)).collect(Collectors.toList());
    }
}
