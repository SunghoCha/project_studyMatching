package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidPrincipalTypeException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_PRINCIPAL_TYPE;

    public InvalidPrincipalTypeException() {
        super(errorCode);
    }

}
