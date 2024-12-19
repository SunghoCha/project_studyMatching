package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidEnrollmentStateException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_ENROLLMENT_STATE;

    public InvalidEnrollmentStateException() {
        super(errorCode);
    }
}
