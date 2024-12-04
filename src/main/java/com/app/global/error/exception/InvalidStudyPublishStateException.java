package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidStudyPublishStateException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_STUDY_PUBLISH_STATE;

    public InvalidStudyPublishStateException() {
        super(errorCode);
    }

}
