package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class UserZoneNotFoundException extends BusinessException {

    public UserZoneNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
