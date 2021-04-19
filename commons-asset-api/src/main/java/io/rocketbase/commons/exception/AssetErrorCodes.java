package io.rocketbase.commons.exception;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AssetErrorCodes {
    INVALID_CONTENT_TYPE("invalid_content_type", 3001),
    NOT_ALLOWED_CONTENT_TYPE("not_allowed_content_type", 3002),
    ASSET_FILE_IS_EMPTY("asset_file_is_empty", 3010),
    @Deprecated
    SYSTEM_REF_ID_ALREADY_USED("system_ref_id_already_used", 3020),
    UNPROCESSABLE_ASSET("unprocessable_asset", 3030),
    NOT_DOWNLOADABLE("not_downloadable", 3040);

    @Getter
    @JsonValue
    private final String value;

    @Getter
    private final int status;

}
