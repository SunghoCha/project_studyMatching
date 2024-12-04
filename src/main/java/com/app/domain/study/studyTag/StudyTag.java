package com.app.domain.study.studyTag;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.study.Study;
import com.app.domain.tag.Tag;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@EqualsAndHashCode(of = {"study", "tag"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTag extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "study_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    private StudyTag(Study study, Tag tag) {
        this.study = study;
        this.tag = tag;
    }

    public void disconnectStudy() {
        this.study = null;
    }
}
