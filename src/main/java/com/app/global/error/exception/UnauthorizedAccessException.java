package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class UnauthorizedAccessException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.UNAUTHORIZED_ACCESS_EXCEPTION;

    public UnauthorizedAccessException() {
        super(errorCode);
    }

}
