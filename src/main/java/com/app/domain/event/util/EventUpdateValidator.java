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
        EventCreateRequest request = (EventCreateRequest) target;

        eventValidationUtils.validateEventDates(
                request.getEndEnrollmentDateTime(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                errors);
    }
}
