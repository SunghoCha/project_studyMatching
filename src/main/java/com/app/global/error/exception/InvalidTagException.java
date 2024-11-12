package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidTagException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_TAG;

    public InvalidTagException() {
        super(errorCode);
    }

}
