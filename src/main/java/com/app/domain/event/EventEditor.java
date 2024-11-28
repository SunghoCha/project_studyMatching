package com.app.domain.event;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class EventEditor {

    private String title;
    private String description;
    private LocalDateTime endEnrollmentDateTime;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Integer limitOfEnrollments;

    public EventEditor(String title, String description, LocalDateTime endEnrollmentDateTime,
                       LocalDateTime startDateTime, LocalDateTime endDateTime, Integer limitOfEnrollments) {
        this.title = title;
        this.description = description;
        this.endEnrollmentDateTime = endEnrollmentDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.limitOfEnrollments = limitOfEnrollments;
    }

    public static EventEditorBuilder builder() {
        return new EventEditorBuilder();
    }

    public static class EventEditorBuilder {

        private String title;
        private String description;
        private LocalDateTime endEnrollmentDateTime;
        private LocalDateTime startDateTime;
        private LocalDateTime endDateTime;
        private Integer limitOfEnrollments;

        public EventEditorBuilder title(String title) {
            if (title != null && !title.isBlank()) {
                this.title = title;
            }
            return this;
        }

        public EventEditorBuilder description(String description) {
            if (description != null && !description.isBlank()) {
                this.description = description;
            }
            return this;
        }

        public EventEditorBuilder endErollmentDateTime(LocalDateTime endEnrollmentDateTime) {
            if (endEnrollmentDateTime != null) {
                this.endEnrollmentDateTime = endEnrollmentDateTime;
            }
            return this;
        }

        public EventEditorBuilder startDateTime(LocalDateTime startDateTime) {
            if (startDateTime != null) {
                this.startDateTime = startDateTime;
            }
            return this;
        }

        public EventEditorBuilder endDateTime(LocalDateTime endDateTime) {
            if (endDateTime != null) {
                this.endDateTime = endDateTime;
            }
            return this;
        }

        public EventEditorBuilder limitOfEnrollments(Integer limitOfEnrollments) {
            if (limitOfEnrollments != null && limitOfEnrollments > 1) {
                this.limitOfEnrollments = limitOfEnrollments;
            }
            return this;
        }

        public EventEditor build() {
            return EventEditor.builder()
                    .title(title)
                    .description(description)
                    .endErollmentDateTime(endEnrollmentDateTime)
                    .startDateTime(startDateTime)
                    .endDateTime(endDateTime)
                    .limitOfEnrollments(limitOfEnrollments)
                    .build();
        }
    }
}
