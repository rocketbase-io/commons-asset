package io.rocketbase.commons.converter;

import com.squareup.pollexor.Thumbor;
import io.rocketbase.commons.config.AssetConfiguration;
import io.rocketbase.commons.dto.asset.*;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.MongoFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssetConverter {

    private AssetConfiguration assetConfiguration;

    private FileStorageService fileStorageService;

    private Thumbor thumbor;

    private List<PreviewSize> defaultSizes = Arrays.asList(PreviewSize.S, PreviewSize.M, PreviewSize.L);

    @Autowired
    public AssetConverter(AssetConfiguration assetConfiguration, FileStorageService fileStorageService) {
        this.assetConfiguration = assetConfiguration;
        this.fileStorageService = fileStorageService;
    }

    private boolean useLocalEndpoint() {
        return fileStorageService instanceof MongoFileStorageService;
    }

    public AssetRead fromEntity(AssetEntity entity, List<PreviewSize> sizes, String baseUrl) {
        if (entity == null) {
            return null;
        }
        AssetPreviews assetPreviews = AssetPreviews.builder()
                .previewMap(new HashMap<>())
                .build();

        ((sizes == null || sizes.isEmpty()) ? defaultSizes : sizes)
                .forEach(s -> assetPreviews.getPreviewMap()
                        .put(s, getPreviewUrl(entity.getId(), entity.getUrlPath(), s, baseUrl)));

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
                .previews(assetPreviews)
                .build();
    }

    public List<AssetRead> fromEntities(List<AssetEntity> entities, List<PreviewSize> sizes, String baseUrl) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(v -> fromEntity(v, sizes, baseUrl)).collect(Collectors.toList());
    }

    public AssetRead toRead(AssetReference reference, String baseUrl) {
        return toRead(reference, null, baseUrl);
    }

    public AssetRead toRead(AssetReference reference, List<PreviewSize> sizes, String baseUrl) {
        if (reference == null) {
            return null;
        }
        AssetPreviews assetPreviews = AssetPreviews.builder()
                .previewMap(new HashMap<>())
                .build();

        ((sizes == null || sizes.isEmpty()) ? defaultSizes : sizes)
                .forEach(s -> assetPreviews.getPreviewMap()
                        .put(s, getPreviewUrl(reference.getId(), reference.getUrlPath(), s, baseUrl)));

        return AssetRead.builderRead()
                .id(reference.getId())
                .systemRefId(reference.getSystemRefId())
                .urlPath(reference.getUrlPath())
                .type(reference.getType())
                .meta(reference.getMeta())
                .previews(assetPreviews)
                .build();
    }

    private String getPreviewUrl(String id, String urlPath, PreviewSize size, String baseUrl) {
        if (useLocalEndpoint()) {
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            return baseUrl + assetConfiguration.getRenderEndpoint() + "/" + id + "/" + size.name().toLowerCase();
        } else {
            return getThumbor().buildImage(urlPath)
                    .resize(size.getMaxWidth(), size.getMaxHeight())
                    .fitIn()
                    .toUrl();
        }
    }

    private Thumbor getThumbor() {
        if (thumbor == null) {
            String thumborKey = assetConfiguration.getThumborKey();
            if (thumborKey.isEmpty()) {
                thumbor = Thumbor.create(assetConfiguration.getThumborHost());
            } else {
                thumbor = Thumbor.create(assetConfiguration.getThumborHost(), thumborKey);
            }
        }
        return thumbor;
    }
}
