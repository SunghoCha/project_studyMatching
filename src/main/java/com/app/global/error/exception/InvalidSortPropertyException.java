package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidSortPropertyException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_END_ENROLLMENT_DATE_TIME;

    public InvalidSortPropertyException() {
        super(errorCode);
    }

}
