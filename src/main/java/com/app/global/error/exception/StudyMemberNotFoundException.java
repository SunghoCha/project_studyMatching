package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class StudyMemberNotFoundException extends BusinessException{

    private static final ErrorCode errorCode = ErrorCode.STUDY_MEMBER_NOT_EXISTS;

    public StudyMemberNotFoundException() {
        super(errorCode);
    }
}
