package io.rocketbase.commons.controller.exceptionhandler;

import io.rocketbase.commons.dto.ErrorResponse;
import io.rocketbase.commons.exception.AssetErrorCodes;
import io.rocketbase.commons.exception.SystemRefIdAlreadyUsedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class SystemRefIdAlreadyUsedExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleRegistrationException(HttpServletRequest request, SystemRefIdAlreadyUsedException e) {
        return ErrorResponse.builder()
                .status(AssetErrorCodes.SYSTEM_REF_ID_ALREADY_USED.getStatus())
                .message(translate(request, "error.systemRefIdUsed", "given systemRefId is already used"))
                .field("systemRefId", "is already used - take other")
                .build();
    }
}
