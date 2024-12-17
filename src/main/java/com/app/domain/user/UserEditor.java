package com.app.domain.user;

import lombok.Getter;

@Getter
public class UserEditor {

    private boolean studyCreatedByEmail;
    private boolean studyCreatedByWeb;
    private boolean studyEnrollmentResultByEmail;
    private boolean studyEnrollmentResultByWeb;
    private boolean studyUpdatedByEmail;
    private boolean studyUpdatedByWeb;

    public UserEditor(boolean studyCreatedByEmail, boolean studyCreatedByWeb,
                      boolean studyEnrollmentResultByEmail, boolean studyEnrollmentResultByWeb,
                      boolean studyUpdatedByEmail, boolean studyUpdatedByWeb) {
        this.studyCreatedByEmail = studyCreatedByEmail;
        this.studyCreatedByWeb = studyCreatedByWeb;
        this.studyEnrollmentResultByEmail = studyEnrollmentResultByEmail;
        this.studyEnrollmentResultByWeb = studyEnrollmentResultByWeb;
        this.studyUpdatedByEmail = studyUpdatedByEmail;
        this.studyUpdatedByWeb = studyUpdatedByWeb;
    }

    public static UserEditorBuilder builder() {
        return new UserEditorBuilder();
    }

    public static class UserEditorBuilder {

        private boolean studyCreatedByEmail;
        private boolean studyCreatedByWeb;
        private boolean studyEnrollmentResultByEmail;
        private boolean studyEnrollmentResultByWeb;
        private boolean studyUpdatedByEmail;
        private boolean studyUpdatedByWeb;

        public UserEditorBuilder studyCreatedByEmail(Boolean studyCreatedByEmail) {
            if (studyCreatedByEmail != null) {
                this.studyCreatedByEmail = studyCreatedByEmail;
            }
            return this;
        }

        public UserEditorBuilder studyCreatedByWeb(Boolean studyCreatedByWeb) {
            if (studyCreatedByWeb != null) {
                this.studyCreatedByWeb = studyCreatedByWeb;
            }
            return this;
        }

        public UserEditorBuilder studyEnrollmentResultByEmail(Boolean studyEnrollmentResultByEmail) {
            if (studyEnrollmentResultByEmail != null) {
                this.studyEnrollmentResultByEmail = studyEnrollmentResultByEmail;
            }
            return this;
        }

        public UserEditorBuilder studyEnrollmentResultByWeb(Boolean studyEnrollmentResultByWeb) {
            if (studyEnrollmentResultByWeb != null) {
                this.studyEnrollmentResultByWeb = studyEnrollmentResultByWeb;
            }
            return this;
        }

        public UserEditorBuilder studyUpdatedByEmail(Boolean studyUpdatedByEmail) {
            if (studyUpdatedByEmail != null) {
                this.studyUpdatedByEmail = studyUpdatedByEmail;
            }
            return this;
        }

        public UserEditorBuilder studyUpdatedByWeb(Boolean studyUpdatedByWeb) {
            if (studyUpdatedByWeb != null) {
                this.studyUpdatedByWeb = studyUpdatedByWeb;
            }
            return this;
        }

        public UserEditor build() {
            return new UserEditor(studyCreatedByEmail, studyCreatedByWeb,
                    studyEnrollmentResultByEmail, studyEnrollmentResultByWeb,
                    studyUpdatedByEmail, studyUpdatedByWeb);
        }
    }


}
