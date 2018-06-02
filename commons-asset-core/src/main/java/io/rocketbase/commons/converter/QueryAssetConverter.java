package io.rocketbase.commons.converter;

import io.rocketbase.commons.controller.BaseController;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.QueryAsset;
import org.springframework.util.MultiValueMap;

public class QueryAssetConverter implements BaseController {

    /*

    private LocalDateTime before;
    private LocalDateTime after;
    private String originalFilename;
    private String referenceUrl;
    @Singular
    private List<AssetType> types;
     */
    public QueryAsset fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        QueryAsset.QueryAssetBuilder builder = QueryAsset.builder()
                .before(parseLocalDateTime(params, "before", null))
                .after(parseLocalDateTime(params, "after", null))
                .originalFilename(params.containsKey("originalFilename") ? params.getFirst("originalFilename") : null)
                .originalFilename(params.containsKey("referenceUrl") ? params.getFirst("referenceUrl") : null);

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
