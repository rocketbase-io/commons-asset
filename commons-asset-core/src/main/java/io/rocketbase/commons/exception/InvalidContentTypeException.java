package io.rocketbase.commons.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class InvalidContentTypeException extends RuntimeException {

    private final String contentType;
}
