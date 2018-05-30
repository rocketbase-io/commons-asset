package io.rocketbase.commons.exception;

import lombok.Getter;

public enum AssetErrorCodes {
    INVALID_CONTENT_TYPE(3001),
    NOT_ALLOWED_CONTENT_TYPE(3002),
    ASSET_FILE_IS_EMPTY(3010),
    SYSTEM_REF_ID_ALREADY_USED(3020),
    UNPROCESSABLE_ASSET(3030),
    NOT_DOWNLOADABLE(3040);

    @Getter
    private int status;

    AssetErrorCodes(int status) {
        this.status = status;
    }
}
