package com.app.domain.study.dto.studySetting;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Getter
@NoArgsConstructor
public class StudyPathUpdateRequest {

    public static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$";

    @NotBlank(message = "경로는 비워둘 수 없습니다.")
    @Length(min = 2, max = 20, message = "경로는 {min}자 이상 {max}자 이하여야 합니다.")
    @Pattern(regexp = VALID_PATH_PATTERN, message = "경로는 올바른 형식이어야 합니다.")
    private String path;

    public StudyPathUpdateRequest(String path) {
        this.path = path;
    }
}
