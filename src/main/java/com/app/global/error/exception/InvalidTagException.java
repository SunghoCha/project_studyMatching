package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidTagException extends BusinessException {

    public InvalidTagException(ErrorCode errorCode) {
        super(errorCode);
    }

}
