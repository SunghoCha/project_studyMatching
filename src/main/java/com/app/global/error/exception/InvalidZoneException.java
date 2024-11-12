package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidZoneException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_ZONE;

    public InvalidZoneException() {
        super(errorCode);
    }
}
