package com.app.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 커스텀 예외 참고용 코드
    // 인증 && 인가
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A-001", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A-002", "해당 토큰은 유효한 토큰이 아닙니다."),
    TOKEN_CLAIM_NOT_EXIST(HttpStatus.UNAUTHORIZED, "A-003", "필수 클레임이 누락되었습니다."),
    INVALID_PRINCIPAL_TYPE(HttpStatus.UNAUTHORIZED, "A-004", "유효하지 않은 인증 주체입니다."),
    // 회원
    USER_NOT_EXISTS(HttpStatus.BAD_REQUEST, "M-001", "해당 회원은 존재하지 않습니다."),
//    ALREADY_REGISTERED_USER(HttpStatus.BAD_REQUEST, "M-002", "이미 가입된 회원 입니다."),
//    INVALID_USER_TYPE(HttpStatus.BAD_REQUEST, "M-003", "잘못된 회원 타입 입니다."),

    // Tag
    INVALID_TAG(HttpStatus.BAD_REQUEST, "T-001", "잘못된 태그 입니다."),

    // Zone
    ZONE_NOT_EXISTS(HttpStatus.BAD_REQUEST, "Z-001", "해당 지역은 존재하지 않습니다."),
    INVALID_ZONE(HttpStatus.BAD_REQUEST, "Z-002", "잘못된 지역명 입니다."),

    // Study
    STUDY_PATH_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "S-001", "이미 존재하는 스터디경로입니다."),
    STUDY_NOT_EXISTS(HttpStatus.BAD_REQUEST, "S-001", "해당 스터디는 존재하지 않습니다."),
    UNAUTHORIZED_ACCESS_EXCEPTION(HttpStatus.UNAUTHORIZED, "S-002", "스터디 수정 권한이 없습니다"),
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
