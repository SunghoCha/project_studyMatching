package com.app.domain.study.studyTag.repository;

import com.app.domain.study.dto.StudyQueryResponse;
import com.app.domain.study.studyTag.StudyTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudyTagRepository extends JpaRepository<StudyTag, Long> {


}
