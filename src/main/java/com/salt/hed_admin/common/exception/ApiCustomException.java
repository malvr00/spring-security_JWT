package com.salt.hed_admin.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiCustomException extends RuntimeException {

    private final ErrorEnum errorCode;

    private final String format;

    public ApiCustomException(ErrorEnum errorCode) {
        this.errorCode = errorCode;
        this.format = null;
    }
}
