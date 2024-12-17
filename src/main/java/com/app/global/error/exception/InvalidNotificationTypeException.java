package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidNotificationTypeException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_NOTIFICATION_TYPE;

    public InvalidNotificationTypeException() {
        super(errorCode);
    }

}
