package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class EmailSendException extends BusinessException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_EMAIL_SEND_CONDITION;

    public EmailSendException() {
        super(errorCode);
    }

}
