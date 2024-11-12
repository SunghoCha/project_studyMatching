package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class UserZoneNotFoundException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.ZONE_NOT_EXISTS;

    public UserZoneNotFoundException() {
        super(errorCode);
    }
}
