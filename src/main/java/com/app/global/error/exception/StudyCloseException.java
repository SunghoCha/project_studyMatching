package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class StudyCloseException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_STUDY_CLOSE_STATE;

    public StudyCloseException() {
        super(errorCode);
    }

}
