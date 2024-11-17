package com.app.domain.study.studyManager;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.study.Study;
import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@EqualsAndHashCode(of = {"user", "study"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyManager extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_manager_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @Builder
    public StudyManager(User user, Study study) {
        this.user = user;
        this.study = study;
    }

    public static StudyManager createManager(User user, Study study) {
        return StudyManager.builder()
                .user(user)
                .study(study)
                .build();
    }

    public boolean isSameUser(User user) {
        return this.user.equals(user);
    }
}
