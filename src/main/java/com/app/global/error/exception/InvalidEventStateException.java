package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidEventStateException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_EVENT_STATE;

    public InvalidEventStateException() {
        super(errorCode);
    }

}
