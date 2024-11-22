package com.app.domain.study.studyZone.repository;

import com.app.domain.study.studyZone.StudyZone;

import java.util.List;
import java.util.Set;

public interface StudyZoneRepositoryCustom {

    List<StudyZone> findByStudyAndZoneIds(Long studyId, Set<Long> zoneIds);
    Long deleteAllByIds(Set<Long> zoneIds);
}
