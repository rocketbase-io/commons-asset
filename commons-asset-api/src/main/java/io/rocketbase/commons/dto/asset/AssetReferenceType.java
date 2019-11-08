package io.rocketbase.commons.dto.asset;

public interface AssetReferenceType {

    String getId();

    String getSystemRefId();

    String getUrlPath();

    AssetType getType();

    String getContext();

    AssetMeta getMeta();
}
