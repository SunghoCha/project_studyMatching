package com.app.domain.study.studyZone.repository;

import com.app.domain.study.Study;
import com.app.domain.study.studyZone.StudyZone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface StudyZoneRepository extends JpaRepository<StudyZone, Long>, StudyZoneRepositoryCustom {

    List<StudyZone> findAllByStudy(Study study);

}
