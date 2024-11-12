package com.app.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 커스텀 예외 참고용 코드
    // 인증 && 인가
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A-001", "토큰이 만료되었습니다."),
    NOT_VALID_TOKEN(HttpStatus.UNAUTHORIZED, "A-002", "해당 토큰은 유효한 토큰이 아닙니다."),
    //    NOT_EXISTS_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "A-003", "Authorization Header가 빈값입니다."),
//    NOT_VALID_BEARER_GRANT_TYPE(HttpStatus.UNAUTHORIZED, "A-004", "인증 타입이 Bearer 타입이 아닙니다."),
//    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A-005", "해당 refresh token은 존재하지 않습니다."),
//    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A-006", "해당 refresh token은 만료됐습니다."),
//    NOT_ACCESS_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "A-007", "해당 토큰은 ACCESS TOKEN이 아닙니다."),
//    FORBIDDEN_ADMIN(HttpStatus.FORBIDDEN, "A-008", "관리자 Role이 아닙니다."),
//
//    // 회원
    USER_NOT_EXISTS(HttpStatus.BAD_REQUEST, "M-001", "해당 회원은 존재하지 않습니다."),
//    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "M-002", "이미 가입된 회원 입니다."),
//    INVALID_USER_TYPE(HttpStatus.BAD_REQUEST, "M-003", "잘못된 회원 타입 입니다."),

    // Tag
    INVALID_TAG(HttpStatus.BAD_REQUEST, "T-001", "잘못된 태그 입니다."),

    // Zone
    ZONE_NOT_EXISTS(HttpStatus.BAD_REQUEST, "Z-001", "해당 지역은 존재하지 않습니다."),
    INVALID_ZONE(HttpStatus.BAD_REQUEST, "T-002", "잘못된 지역명 입니다."),

    // Study
    STUDY_PATH_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "S-001", "이미 존재하는 스터디경로입니다."),
    ;

    ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
