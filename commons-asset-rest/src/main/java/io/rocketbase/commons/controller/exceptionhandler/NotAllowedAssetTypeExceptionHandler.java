package io.rocketbase.commons.controller.exceptionhandler;

import com.google.common.base.Joiner;
import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.NotAllowedAssetTypeException;
import io.rocketbase.commons.service.AssetTypeFilterService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class NotAllowedAssetTypeExceptionHandler extends BaseExceptionHandler {

    @Resource
    private AssetTypeFilterService assetTypeFilterService;

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleRegistrationException(HttpServletRequest request, NotAllowedAssetTypeException e) {
        return ErrorResponse.builder()
                .status(AssetErrorCodes.NOT_ALLOWED_CONTENT_TYPE.getStatus())
                .message(translate(request, "error.notAllowedContentType", "not allowed content type"))
                .field("assetType", e.getAssetType().name())
                .field("allowedTypes", Joiner.on(", ").join(assetTypeFilterService.getAllowedAssetTypes()))
                .build();
    }
}
