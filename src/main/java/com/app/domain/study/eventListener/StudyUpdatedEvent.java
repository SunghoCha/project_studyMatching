package com.app.domain.study.eventListener;

import com.app.domain.study.Study;
import lombok.Getter;

@Getter
public class StudyUpdatedEvent {

    private final Study study;
    private final String message;

    public StudyUpdatedEvent(Study study, String message) {
        this.study = study;
        this.message = message;
    }
}
