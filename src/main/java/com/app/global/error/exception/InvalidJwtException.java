package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidJwtException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_TOKEN;

    public InvalidJwtException() {
        super(errorCode);
    }

}
