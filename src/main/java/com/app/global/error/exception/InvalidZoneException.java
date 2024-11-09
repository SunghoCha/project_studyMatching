package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class InvalidZoneException extends BusinessException {

    public InvalidZoneException(ErrorCode errorCode) {
        super(errorCode);
    }
}
