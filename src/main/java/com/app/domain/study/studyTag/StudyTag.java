package com.app.domain.study.studyTag;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.study.Study;
import com.app.domain.tag.Tag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(of = {"study", "tag"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyTag extends BaseTimeEntity {

    @Id @GeneratedValue
    @Column(name = "study_tag_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;
}
