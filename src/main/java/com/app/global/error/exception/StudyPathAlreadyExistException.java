package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class StudyPathAlreadyExistException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.STUDY_PATH_ALREADY_EXISTS;

    public StudyPathAlreadyExistException() {
        super(errorCode);
    }
}
