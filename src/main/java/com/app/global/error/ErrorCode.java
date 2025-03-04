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
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "S-002", "스터디 수정 권한이 없습니다"),
    INVALID_STUDY_PUBLISH_STATE(HttpStatus.BAD_REQUEST, "S-003", "스터디 공개 상태가 올바르지 않습니다."),
    INVALID_STUDY_CLOSE_STATE(HttpStatus.BAD_REQUEST, "S-004", "스터디를 종료할 수 없는 상태입니다."),
    INVALID_SORT_PROPERTY(HttpStatus.BAD_REQUEST, "S-005", "정렬 조건이 올바르지 않습니다."),
    INVALID_STUDY_JOIN_CONDITION(HttpStatus.BAD_REQUEST, "S-006", "스터디에 가입할 수 있는 조건을 만족하지 않습니다."),

    // StudyMember
    STUDY_MEMBER_NOT_EXISTS(HttpStatus.BAD_REQUEST, "SM-001", "해당 스터디멤버는 존재하지 않습니다."),

    // Event
    INVALID_END_ENROLLMENT_DATE_TIME(HttpStatus.BAD_REQUEST, "E-001", "유효하지 않은 모임 접수 종료 날짜입니다."),
    INVALID_END_DATE_TIME(HttpStatus.BAD_REQUEST, "E-002", "유효하지 않은 모임 종료 날짜입니다."),
    INVALID_START_DATE_TIME(HttpStatus.BAD_REQUEST, "E-003", "유효하지 않은 모임 시작 날짜입니다."),
    EVENT_NOT_EXISTS(HttpStatus.BAD_REQUEST, "E-004", "해당 이벤트는 존재하지 않습니다."),
    ENROLLMENT_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "E-005", "이미 존재하는 등록입니다."),
    INVALID_ENROLLMENT(HttpStatus.BAD_REQUEST, "E-006", "유효하지 않는 등록입니다."),
    INVALID_RECRUITMENT_STATE(HttpStatus.BAD_REQUEST, "E-007", "유효하지 않은 모집 상태입니다."),
    INVALID_EVENT_STATE(HttpStatus.BAD_REQUEST, "E-008", "이벤트 참가자가 있으면 이벤트를 삭제할 수 없습니다."),


    // Email
    INVALID_EMAIL_SEND_CONDITION(HttpStatus.BAD_REQUEST, "EM-001", "이메일 전송에 실패하였습니다."),

    // Notification
    NOTIFICATION_NOT_EXIST(HttpStatus.BAD_REQUEST, "N-002", "해당 알림은 존재하지 않습니다."),

    // Enrollment
    INVALID_ENROLLMENT_STATE(HttpStatus.BAD_REQUEST, "EN-001", "참가 조건이 유효하지 않습니다."),

    // FileUpload
    INVALID_FILE_UPLOAD(HttpStatus.BAD_REQUEST, "F-001", "파일 업로드 오류가 발생하였습니다.");

    ErrorCode(HttpStatus httpStatus, String errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String message;
}
