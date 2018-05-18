package io.rocketbase.commons.controller.exceptionhandler;

import com.google.common.base.Joiner;
import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.dto.asset.AssetType;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.InvalidContentTypeException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class InvalidContentTypeExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleRegistrationException(HttpServletRequest request, InvalidContentTypeException e) {
        return ErrorResponse.builder()
                .status(AssetErrorCodes.INVALID_CONTENT_TYPE.getStatus())
                .message(translate(request, "error.invalidContentType", "invalid content type"))
                .field("contentType", e.getContentType())
                .field("supportedTypes", Joiner.on(", ").join(AssetType.values()))
                .build();
    }
}
