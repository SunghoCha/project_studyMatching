package com.app.domain.study.dto.studySetting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class StudyTitleUpdateRequest {

    @NotBlank
    @Length(max = 15)
    private String title;

    public StudyTitleUpdateRequest(String title) {
        this.title = title;
    }
}
