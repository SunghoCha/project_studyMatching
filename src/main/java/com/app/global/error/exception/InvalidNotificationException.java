package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidNotificationException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.NOTIFICATION_NOT_EXIST;

    public InvalidNotificationException() {
        super(errorCode);
    }

}
