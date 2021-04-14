package io.rocketbase.commons.converter;

import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.dto.asset.QueryAsset;
import io.rocketbase.commons.util.QueryParamBuilder;
import io.rocketbase.commons.util.QueryParamParser;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashSet;
import java.util.Set;

public class QueryAssetConverter {

    public static UriComponentsBuilder addParams(UriComponentsBuilder uriBuilder, QueryAsset query) {
        if (query != null) {
            QueryParamBuilder.appendParams(uriBuilder, "before", query.getBefore());
            QueryParamBuilder.appendParams(uriBuilder, "after", query.getAfter());
            QueryParamBuilder.appendParams(uriBuilder, "originalFilename", query.getOriginalFilename());
            QueryParamBuilder.appendParams(uriBuilder, "referenceUrl", query.getReferenceUrl());
            QueryParamBuilder.appendParams(uriBuilder, "context", query.getContext());
            QueryParamBuilder.appendParams(uriBuilder, "systemRefId", query.getSystemRefId());
            if (query.getTypes() != null) {
                uriBuilder.queryParam("type", query.getTypes());
            }
            QueryParamBuilder.appendParams(uriBuilder, "hasEolValue", query.getHasEolValue());
            QueryParamBuilder.appendParams(uriBuilder, "keyValue", query.getKeyValues());
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
                .systemRefId(params.getFirst("systemRefId"))
                .originalFilename(params.getFirst("originalFilename"))
                .context(params.getFirst("context"))
                .referenceUrl(params.getFirst("referenceUrl"))
                .hasEolValue(QueryParamParser.parseBoolean(params, "hasEolValue", null))
                .isEol(QueryParamParser.parseBoolean(params, "isEol", null))
                .keyValues(QueryParamParser.parseKeyValue("keyValue", params));

        if (params.containsKey("type")) {
            Set<AssetType> types = new HashSet<>();
            for (String t : params.get("type")) {
                try {
                    types.add(AssetType.valueOf(t.toUpperCase()));
                } catch (Exception e) {
                }
            }
            builder.types(types);
        }
        return builder.build();
    }
}
