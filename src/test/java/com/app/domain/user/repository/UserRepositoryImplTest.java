package com.app.domain.user.repository;

import com.app.TestUtils;
import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.tag.Tag;
import com.app.domain.user.User;
import com.app.domain.zone.Zone;
import com.app.global.error.exception.StudyNotFoundException;
import com.app.global.error.exception.UserNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserRepositoryImplTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    private StudyRepository studyRepository;
    @Autowired
    private StudyManagerRepository studyManagerRepository;

    @Test
    @WithAccount
    @DisplayName("")
    void findUserByTagsAndZones() {
        // given
        String path = "test";
        User user = userRepository.findByEmail(TestUtils.getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("제목1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, savedStudy));
        savedStudy.addManager(savedManager);
        study.publish();

        Study foundStudy = studyRepository.findStudyWithTagsAndZonesById(savedStudy.getId()).orElseThrow(StudyNotFoundException::new);
        List<Tag> tags = foundStudy.getStudyTags().stream().map(StudyTag::getTag).toList();
        List<Zone> zones = foundStudy.getStudyZones().stream().map(StudyZone::getZone).toList();

        // when
        List<User> foundUser = userRepository.findUserByTagsAndZones(tags, zones);

        // then
        Assertions.assertThat(foundUser).hasSize(0);
    }
}