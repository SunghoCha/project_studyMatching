package com.app.domain.study.repository;

import com.app.domain.study.Study;

import java.util.Optional;

public interface StudyRepositoryCustom {

    Optional<Study> findStudyWithManagerByPath(String path);
    Optional<Study> findStudyWithAllByPath(String path);
    Optional<Study> findStudyWithTagsAndZonesById(Long id);
}
