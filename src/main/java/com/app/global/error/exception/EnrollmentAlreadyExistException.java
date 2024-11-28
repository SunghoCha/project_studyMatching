package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class EnrollmentAlreadyExistException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.STUDY_PATH_ALREADY_EXISTS;

    public EnrollmentAlreadyExistException() {
        super(errorCode);
    }
}
