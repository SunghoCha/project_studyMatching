package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class EventNotFoundException extends BusinessException{

    private static final ErrorCode errorCode = ErrorCode.EVENT_NOT_EXISTS;

    public EventNotFoundException() {
        super(errorCode);
    }
}
