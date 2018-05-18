package io.rocketbase.commons.converter;

import com.squareup.pollexor.Thumbor;
import io.rocketbase.commons.config.AssetConfiguration;
import io.rocketbase.commons.dto.asset.AssetMeta;
import io.rocketbase.commons.dto.asset.AssetPreviews;
import io.rocketbase.commons.dto.asset.AssetRead;
import io.rocketbase.commons.dto.asset.PreviewSize;
import io.rocketbase.commons.model.AssetEntity;
import io.rocketbase.commons.service.FileStorageService;
import io.rocketbase.commons.service.MongoFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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

    public AssetRead fromEntity(AssetEntity entity, List<PreviewSize> sizes, HttpServletRequest request) {
        if (entity == null) {
            return null;
        }
        AssetPreviews assetPreviews = AssetPreviews.builder()
                .previewMap(new HashMap<>())
                .build();

        ((sizes == null || sizes.isEmpty()) ? defaultSizes : sizes)
                .forEach(s -> assetPreviews.getPreviewMap()
                        .put(s, getPreviewUrl(entity, s, request)));

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

    public List<AssetRead> fromEntities(List<AssetEntity> entities, List<PreviewSize> sizes, HttpServletRequest request) {
        if (entities == null) {
            return null;
        }
        return entities.stream().map(v -> fromEntity(v, sizes, request)).collect(Collectors.toList());
    }

    private String getPreviewUrl(AssetEntity entity, PreviewSize size, HttpServletRequest request) {
        if (useLocalEndpoint()) {
            return getBaseUrl(request) + assetConfiguration.getRenderEndpoint() + "/" + entity.getId() + "/" + size.name().toLowerCase();
        } else {
            return getThumbor().buildImage(entity.getUrlPath())
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

    private String getBaseUrl(HttpServletRequest request) {
        String result = request.getScheme() + "://" + request.getServerName();
        int serverPort = request.getServerPort();
        if (serverPort != 80 && serverPort != 443) {
            result += ":" + serverPort;
        }
        result += request.getContextPath();
        if (result.endsWith("/")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }
}
