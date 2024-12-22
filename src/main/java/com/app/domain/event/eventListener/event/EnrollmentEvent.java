package com.app.domain.event.eventListener.event;

import com.app.domain.event.Enrollment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class EnrollmentEvent {

    protected final Enrollment enrollment;
    protected final String message;
}
