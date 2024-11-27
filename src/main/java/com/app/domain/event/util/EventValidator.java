package com.app.domain.event.util;


import com.app.domain.event.dto.EventCreateRequest;
import com.app.global.error.ErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import java.time.LocalDateTime;

@Component
public class EventValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return EventCreateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EventCreateRequest request = (EventCreateRequest) target;

        if (isNotValidEndEnrollmentDateTime(request)) {
            errors.rejectValue("endEnrollmentDateTime",
                    ErrorCode.INVALID_END_ENROLLMENT_DATE_TIME.getErrorCode(),
                    ErrorCode.INVALID_END_ENROLLMENT_DATE_TIME.getMessage());
        }

        if (isNotValidEndDateTime(request)) {
            errors.rejectValue("endDateTime",
                    ErrorCode.INVALID_END_DATE_TIME.getErrorCode(),
                    ErrorCode.INVALID_END_DATE_TIME.getMessage());
        }

        if (isNotValidStartDateTime(request)) {
            errors.rejectValue("startDateTime",
                    ErrorCode.INVALID_START_DATE_TIME.getErrorCode(),
                    ErrorCode.INVALID_START_DATE_TIME.getMessage());
        }
    }

    private boolean isNotValidStartDateTime(EventCreateRequest request) {
        return request.getStartDateTime().isBefore(request.getEndEnrollmentDateTime());
    }

    private boolean isNotValidEndDateTime(EventCreateRequest request) {
        LocalDateTime endDateTime = request.getEndDateTime();
        return endDateTime.isBefore(request.getStartDateTime()) || endDateTime.isBefore(request.getEndEnrollmentDateTime());
    }

    private boolean isNotValidEndEnrollmentDateTime(EventCreateRequest request) {
        return request.getEndEnrollmentDateTime().isBefore(LocalDateTime.now());
    }
}
