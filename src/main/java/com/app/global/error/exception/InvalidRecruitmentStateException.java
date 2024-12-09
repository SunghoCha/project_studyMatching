package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidRecruitmentStateException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_RECRUITMENT_STATE;

    public InvalidRecruitmentStateException() {
        super(errorCode);
    }

}
