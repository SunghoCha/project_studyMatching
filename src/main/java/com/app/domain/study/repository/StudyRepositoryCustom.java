package com.app.domain.study.repository;

import com.app.domain.study.Study;

import java.util.Optional;

public interface StudyRepositoryCustom {

    Optional<Study> findByStudyWithManagerByPath(String path);
}
