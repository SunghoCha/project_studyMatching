package com.app.domain.study.repository;

import com.app.domain.study.Study;
import com.app.domain.study.dto.StudyQueryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudyRepository extends JpaRepository<Study, Long> {
    Optional<Study> findByPath(String path);


}
