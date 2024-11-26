package com.app.domain.study.studyTag.repository;

import com.app.domain.study.studyTag.StudyTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyTagRepository extends JpaRepository<StudyTag, Long>, StudyTagRepositoryCustom {


}
