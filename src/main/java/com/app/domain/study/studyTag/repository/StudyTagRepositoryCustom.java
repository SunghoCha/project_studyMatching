package com.app.domain.study.studyTag.repository;

import com.app.domain.study.Study;
import com.app.domain.study.studyTag.StudyTag;

import java.util.List;
import java.util.Set;

public interface StudyTagRepositoryCustom {

    Long deleteAllInStudy(Study study);
    List<StudyTag> findAllByStudyPath(String path);
}
