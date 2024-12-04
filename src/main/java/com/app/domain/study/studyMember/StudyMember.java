package com.app.domain.study.studyMember;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.study.Study;
import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@EqualsAndHashCode(of = {"study", "user"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyMember extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private StudyMember(Study study, User user) {
        this.study = study;
        this.user = user;
    }

    public static StudyMember createMember(User user, Study study) {
        return StudyMember.builder()
                .user(user)
                .study(study)
                .build();
    }

    public boolean isSameUser(User user) {
        return this.user.equals(user);
    }

    public void disconnectStudy() {
        this.study = null;
    }
}
