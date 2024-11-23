package com.app.domain.study.dto;

import com.app.domain.study.Study;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter @Setter
@NoArgsConstructor
public class StudyCreateRequest {

    public static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$";

    @NotBlank(message = "경로는 비워둘 수 없습니다.")
    @Length(min = 2, max = 20, message = "경로는 {min}자 이상 {max}자 이하여야 합니다.")
    @Pattern(regexp = VALID_PATH_PATTERN, message = "경로는 올바른 형식이어야 합니다.")
    private String path;

    @NotBlank
    @Length(max = 15)
    private String title;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;

    @Builder
    public StudyCreateRequest(String path, String title, String shortDescription, String fullDescription) {
        this.path = path;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
    }

    public Study toEntity() {
        return Study.builder()
                .path(path)
                .title(title)
                .shortDescription(shortDescription)
                .fullDescription(fullDescription)
                .build();
    }
}
