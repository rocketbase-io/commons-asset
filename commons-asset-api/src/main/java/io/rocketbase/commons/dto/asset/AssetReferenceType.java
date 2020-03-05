package io.rocketbase.commons.dto.asset;

import javax.annotation.Nullable;

public interface AssetReferenceType {

    String getId();

    @Nullable
    String getSystemRefId();

    String getUrlPath();

    AssetType getType();

    String getContext();

    AssetMeta getMeta();
}
