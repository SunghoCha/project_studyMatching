package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class StudyNotFoundException extends BusinessException{

    private static final ErrorCode errorCode = ErrorCode.STUDY_NOT_EXISTS;

    public StudyNotFoundException() {
        super(errorCode);
    }
}
