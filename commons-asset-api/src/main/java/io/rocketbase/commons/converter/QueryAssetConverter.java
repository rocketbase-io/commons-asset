package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.util.QueryParamBuilder;
import io.rocketbase.commons.util.QueryParamParser;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class QueryAssetConverter {

    public static UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, QueryAsset query) {
        if (query != null) {
            QueryParamBuilder.appendParams(uriBuilder, "before", query.getBefore());
            QueryParamBuilder.appendParams(uriBuilder, "after", query.getAfter());
            if (query.getOriginalFilename() != null) {
                uriBuilder.queryParam("originalFilename", query.getOriginalFilename());
            }
            if (query.getReferenceUrl() != null) {
                uriBuilder.queryParam("referenceUrl", query.getReferenceUrl());
            }
            if (query.getContext() != null) {
                uriBuilder.queryParam("context", query.getContext());
            }
            if (query.getTypes() != null) {
                uriBuilder.queryParam("type", query.getTypes());
            }
            if (query.getHasEolValue() != null) {
                uriBuilder.queryParam("hasEolValue", query.getHasEolValue());
            }
        }
        return uriBuilder;
    }

    public static QueryAsset fromParams(MultiValueMap<String, String> params) {
        if (params == null) {
            return null;
        }
        QueryAsset.QueryAssetBuilder builder = QueryAsset.builder()
                .before(QueryParamParser.parseInstant(params, "before", null))
                .after(QueryParamParser.parseInstant(params, "after", null))
                .originalFilename(params.containsKey("originalFilename") ? params.getFirst("originalFilename") : null)
                .context(params.containsKey("context") ? params.getFirst("context") : null)
                .referenceUrl(params.containsKey("referenceUrl") ? params.getFirst("referenceUrl") : null)
                .hasEolValue(QueryParamParser.parseBoolean(params, "hasEolValue", null))
                .isEol(QueryParamParser.parseBoolean(params, "isEol", null));

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
