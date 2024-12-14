package com.app.domain.study.repository;

import com.app.domain.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long>, StudyRepositoryCustom {
    Optional<Study> findByPath(String path);

    List<Study> findByPathIn(List<String> pathList);
}
