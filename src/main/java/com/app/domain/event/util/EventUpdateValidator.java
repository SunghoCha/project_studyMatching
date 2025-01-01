package com.app.domain.event.util;


import com.app.domain.event.dto.EventCreateRequest;
import com.app.domain.event.dto.EventUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class EventUpdateValidator implements Validator {

    private final EventValidationUtils eventValidationUtils;

    @Override
    public boolean supports(Class<?> clazz) {
        return EventUpdateRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

        if (target instanceof EventCreateRequest request) {
            eventValidationUtils.validateEventDates(
                    request.getEndEnrollmentDateTime(),
                    request.getStartDateTime(),
                    request.getEndDateTime(),
                    errors);

        } else if (target instanceof EventUpdateRequest request) {
            eventValidationUtils.validateEventDates(
                    request.getEndEnrollmentDateTime(),
                    request.getStartDateTime(),
                    request.getEndDateTime(),
                    errors);
        } else {
            throw new IllegalArgumentException("이벤트 타입이 잘못되었습니다.");
        }
    }
}
