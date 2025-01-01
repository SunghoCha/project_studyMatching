package com.app.domain.event.util;

import com.app.global.error.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDateTime;

@Component
public class EventValidationUtils {

    public void validateEventDates(LocalDateTime endEnrollmentDateTime,
                                   LocalDateTime startDateTime,
                                   LocalDateTime endDateTime,
                                   Errors errors) {

        if (isNotValidEndEnrollmentDateTime(endEnrollmentDateTime)) {
            errors.rejectValue("endEnrollmentDateTime",
                    ErrorCode.INVALID_END_ENROLLMENT_DATE_TIME.getErrorCode(),
                    ErrorCode.INVALID_END_ENROLLMENT_DATE_TIME.getMessage());
        }

        if (isNotValidEndDateTime(endEnrollmentDateTime, startDateTime, endDateTime)) {
            errors.rejectValue("endDateTime",
                    ErrorCode.INVALID_END_DATE_TIME.getErrorCode(),
                    ErrorCode.INVALID_END_DATE_TIME.getMessage());
        }

        if (isNotValidStartDateTime(endEnrollmentDateTime, startDateTime)) {
            errors.rejectValue("startDateTime",
                    ErrorCode.INVALID_START_DATE_TIME.getErrorCode(),
                    ErrorCode.INVALID_START_DATE_TIME.getMessage());
        }
    }

    private boolean isNotValidEndEnrollmentDateTime(LocalDateTime endEnrollmentDateTime) {
        return endEnrollmentDateTime.isBefore(LocalDateTime.now());
    }

    private boolean isNotValidEndDateTime(LocalDateTime endEnrollmentDateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return endDateTime.isBefore(startDateTime) || endDateTime.isBefore(endEnrollmentDateTime);
    }

    private boolean isNotValidStartDateTime(LocalDateTime endEnrollmentDateTime, LocalDateTime startDateTime) {
        return startDateTime.isBefore(endEnrollmentDateTime);
    }
}
