package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidStartDateTimeException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_START_DATE_TIME;

    public InvalidStartDateTimeException() {
        super(errorCode);
    }

}
