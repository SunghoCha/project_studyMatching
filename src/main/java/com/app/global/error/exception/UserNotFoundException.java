package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class UserNotFoundException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.USER_NOT_EXISTS;

    public UserNotFoundException() {
        super(errorCode);
    }

}
