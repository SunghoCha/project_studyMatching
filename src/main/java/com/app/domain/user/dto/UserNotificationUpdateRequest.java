package com.app.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserNotificationUpdateRequest {
    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    @Builder
    public UserNotificationUpdateRequest(boolean studyCreatedByEmail, boolean studyCreatedByWeb,
                                         boolean studyEnrollmentResultByEmail, boolean studyEnrollmentResultByWeb,
                                         boolean studyUpdatedByEmail, boolean studyUpdatedByWeb) {
        this.studyCreatedByEmail = studyCreatedByEmail;
        this.studyCreatedByWeb = studyCreatedByWeb;
        this.studyEnrollmentResultByEmail = studyEnrollmentResultByEmail;
        this.studyEnrollmentResultByWeb = studyEnrollmentResultByWeb;
        this.studyUpdatedByEmail = studyUpdatedByEmail;
        this.studyUpdatedByWeb = studyUpdatedByWeb;
    }
}
