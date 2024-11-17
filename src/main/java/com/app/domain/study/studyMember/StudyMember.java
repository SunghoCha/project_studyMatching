package com.app.domain.study.studyMember;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.study.Study;
import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@EqualsAndHashCode(of = {"study", "user"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyMember extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public boolean isSameUser(User user) {
        return this.user.equals(user);
    }
}
