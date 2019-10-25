package io.rocketbase.commons.converter;

import io.rocketbase.commons.controller.BaseController;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.QueryAsset;
import org.springframework.util.MultiValueMap;

import java.time.ZoneOffset;

public class QueryAssetConverter implements BaseController {

    public QueryAsset fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        QueryAsset.QueryAssetBuilder builder = QueryAsset.builder()
                .before(parseLocalDateTime(params, "before", null).toInstant(ZoneOffset.UTC))
                .after(parseLocalDateTime(params, "after", null).toInstant(ZoneOffset.UTC))
                .originalFilename(params.containsKey("originalFilename") ? params.getFirst("originalFilename") : null)
                .context(params.containsKey("context") ? params.getFirst("context") : null)
                .referenceUrl(params.containsKey("referenceUrl") ? params.getFirst("referenceUrl") : null);

        if (params.containsKey("type")) {
            params.get("type").forEach(t -> {
                try {
                    AssetType assetType = AssetType.valueOf(t.toUpperCase());
                    builder.type(assetType);
                } catch (Exception e) {
                }
            });
        }
        return builder.build();
    }
}
