package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class UnauthorizedAccessException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.UNAUTHORIZED_ACCESS;

    public UnauthorizedAccessException() {
        super(errorCode);
    }

}
