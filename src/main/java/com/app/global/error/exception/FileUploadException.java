package com.app.global.error.exception;

import com.app.global.error.ErrorCode;

public class FileUploadException extends RuntimeException {

    private static final ErrorCode errorCode = ErrorCode.INVALID_FILE_UPLOAD;

    public FileUploadException(Throwable cause) {
        super(errorCode.getMessage(), cause);
    }
}
