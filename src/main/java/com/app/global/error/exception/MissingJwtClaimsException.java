package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class MissingJwtClaimsException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.TOKEN_CLAIM_NOT_EXIST;

    public MissingJwtClaimsException() {
        super(errorCode);
    }

}
