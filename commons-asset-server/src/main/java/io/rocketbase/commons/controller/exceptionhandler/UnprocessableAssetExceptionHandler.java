package io.rocketbase.commons.controller.exceptionhandler;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.UnprocessableAssetException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class UnprocessableAssetExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleRegistrationException(HttpServletRequest request, UnprocessableAssetException e) {
        return ErrorResponse.builder()
                .status(AssetErrorCodes.UNPROCESSABLE_ASSET.getStatus())
                .message(translate(request, "error.unprocessableAsset", "asset unprocessable got internal errors"))
                .build();
    }
}
