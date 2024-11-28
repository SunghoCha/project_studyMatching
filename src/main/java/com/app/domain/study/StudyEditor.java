package com.app.domain.study;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class StudyEditor {

    private String path;
    private String title;
    private String shortDescription;
    private String fullDescription;
    private String image;

    public StudyEditor(String path, String title, String shortDescription, String fullDescription, String image) {
        this.path = path;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.image = image;
    }

    public static StudyEditorBuilder builder() {
        return new StudyEditorBuilder();
    }

    public static class StudyEditorBuilder {
        private String path;
        private String title;
        private String shortDescription;
        private String fullDescription;
        private String image;

        public StudyEditorBuilder path(final String path) {
            if (path != null && !path.isBlank()) {
                this.path = path;
            }
            return this;
        }

        public StudyEditorBuilder title(final String title) {
            if (title != null && !title.isBlank()) {
                this.title = title;
            }
            return this;
        }

        public StudyEditorBuilder shortDescription(final String shortDescription) {
            if (shortDescription != null && !shortDescription.isBlank()) {
                this.shortDescription = shortDescription;
            }
            return this;
        }

        public StudyEditorBuilder fullDescription(final String fullDescription) {
            if (fullDescription != null && !fullDescription.isBlank()) {
                this.fullDescription = fullDescription;
            }
            return this;
        }

        public StudyEditorBuilder image(final String image) {
            if (image != null && !image.isBlank()) {
                this.image = image;
            }
            return this;
        }

        public StudyEditor build() {
            return new StudyEditor(path, title, shortDescription, fullDescription, image);
        }
    }
}
