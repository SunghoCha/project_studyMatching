package com.app.global.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Getter
public class ErrorResponse {

    private String errorCode;
    private String errorMessage;

    @Builder
    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse of(String errorCode, String message) {
        return ErrorResponse.builder()
                .errorCode(errorCode)
                .errorMessage(message)
                .build();
    }

    public static ErrorResponse of(String errorCode, BindingResult bindingResult) {
        return ErrorResponse.of(errorCode, createMessage(bindingResult));
    }

    public static String createMessage(BindingResult bindingResult) {
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (!isFirst) {
                sb.append(", ");
            } else {
                isFirst = false;
            }
            sb.append("[");
            sb.append(fieldError.getField());
            sb.append("] ");
            sb.append(fieldError.getDefaultMessage());
        }

        return sb.toString();
    }


}
