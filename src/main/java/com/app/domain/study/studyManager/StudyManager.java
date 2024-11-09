package com.app.domain.study.studyManager;

import com.app.domain.study.Study;
import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyManager {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_manager_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;
}
