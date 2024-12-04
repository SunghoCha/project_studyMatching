package com.app.domain.study.studyMember.repository;

import com.app.domain.study.Study;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyMemberRepository extends JpaRepository<StudyMember, Long> {
    Optional<StudyMember> findByStudyAndUser(Study study, User user);
}
