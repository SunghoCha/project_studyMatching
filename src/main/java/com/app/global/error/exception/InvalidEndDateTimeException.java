package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidEndDateTimeException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_END_DATE_TIME;

    public InvalidEndDateTimeException() {
        super(errorCode);
    }

}
