package io.rocketbase.commons.controller.exceptionhandler;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.EmptyFileException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class EmptyFileExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleRegistrationException(HttpServletRequest request, EmptyFileException e) {
        return ErrorResponse.builder()
                .status(AssetErrorCodes.ASSET_FILE_IS_EMPTY.getStatus())
                .message(translate(request, "error.emptyAsset", "asset is empty"))
                .field("file", "is empty")
                .build();
    }
}
