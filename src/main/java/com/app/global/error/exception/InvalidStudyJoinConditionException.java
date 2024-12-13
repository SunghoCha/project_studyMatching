package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidStudyJoinConditionException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_STUDY_JOIN_CONDITION;

    public InvalidStudyJoinConditionException() {
        super(errorCode);
    }

}
