package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class AuthenticationException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.TOKEN_EXPIRED;

    public AuthenticationException() {
        super(errorCode);
    }

}
