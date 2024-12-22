package com.app.domain.study.eventListener;

import com.app.domain.study.Study;
import lombok.Getter;

@Getter
public class StudyCreatedEvent {

    private final Study study;

    public StudyCreatedEvent(Study study) {
        this.study = study;
    }
}
