package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidEnrollmentException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_ENROLLMENT;

    public InvalidEnrollmentException() {
        super(errorCode);
    }

}
