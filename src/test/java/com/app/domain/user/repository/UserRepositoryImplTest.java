package com.app.domain.user.repository;

import com.app.TestUtils;
import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyTag.repository.StudyTagRepository;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.study.studyZone.repository.StudyZoneRepository;
import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.domain.user.userTag.UserTag;
import com.app.domain.user.userTag.repository.UserTagRepository;
import com.app.domain.user.userZone.UserZone;
import com.app.domain.user.userZone.repository.UserZoneRepository;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.global.error.exception.StudyNotFoundException;
import com.app.global.error.exception.UserNotFoundException;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.app.TestUtils.*;

@SpringBootTest
@Transactional
class UserRepositoryImplTest {

    @Autowired
    Clock clock;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private StudyManagerRepository studyManagerRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    StudyTagRepository studyTagRepository;

    @Autowired
    StudyZoneRepository studyZoneRepository;
    @Autowired
    private UserTagRepository userTagRepository;
    @Autowired
    private UserZoneRepository userZoneRepository;

    @Test
    @WithAccount
    @DisplayName("스터디의 tag중 최소 1개, zone 중 최소 1개를 자신의 tag와 zone으로 가지는 user를 반환한다. ")
    void findUserByTagsAndZones() {
        // given
        // 태그, 존 세팅
        List<Tag> tagList = tagRepository.saveAll(createTags(9));
        List<Zone> zoneList = zoneRepository.saveAll(createZones(9));

        User user = userRepository.findByEmail(getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);

        // study 세팅
        for (int i = 1; i <= 3; i++) {
            Study study = createStudy("path" + i, user, i);
            Study savedStudy = studyRepository.save(study);

            StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, savedStudy));
            savedStudy.addManager(savedManager);

            Set<StudyTag> studyTags = tagList.subList((i - 1) * 3, i * 3).stream()
                    .map(tag -> StudyTag.builder()
                            .study(savedStudy)
                            .tag(tag)
                            .build())
                    .collect(Collectors.toSet());
            List<StudyTag> savedStudyTags = studyTagRepository.saveAll(studyTags);
            savedStudy.addStudyTags(new HashSet<>(savedStudyTags));

            Set<StudyZone> studyZones = zoneList.subList((i - 1) * 3, i * 3).stream()
                    .map(zone -> StudyZone.builder()
                            .study(savedStudy)
                            .zone(zone)
                            .build())
                    .collect(Collectors.toSet());
            List<StudyZone> savedStudyZones = studyZoneRepository.saveAll(studyZones);
            savedStudy.addStudyZones(new HashSet<>(savedStudyZones));

            study.publish(clock);
        }

        User user1 = createAndSaveUserWithTagsAndZones(user, "1", 0, 3, tagList, zoneList);
        User user2 = createAndSaveUserWithTagsAndZones(user, "2", 0, 2, tagList, zoneList);
        User user3 = createAndSaveUserWithTagsAndZones(user, "3", 2, 5, tagList, zoneList);

        // when
        List<User> foundUser = userRepository.findUserByTagsAndZones(tagList.subList(0, 3), zoneList.subList(0, 3));

        // then
        Assertions.assertThat(foundUser).hasSize(3).extracting("name", "email")
                .containsExactlyInAnyOrder(
                        Tuple.tuple("name1", "email1"),
                        Tuple.tuple("name2", "email2"),
                        Tuple.tuple("name3", "email3")
                );
    }

    private User createAndSaveUserWithTagsAndZones(User user, String alias, int from, int to, List<Tag> tagList, List<Zone> zoneList) {
        User user1 = User.builder()
                .name("name" + alias)
                .email("email" + alias)
                .role(Role.GUEST)
                .build();
        User savedUser = userRepository.save(user1);

        Set<UserTag> userTags = tagList.subList(from, to).stream()
                .map(tag -> UserTag.builder()
                        .user(savedUser)
                        .tag(tag)
                        .build())
                .collect(Collectors.toSet());
        List<UserTag> savedUserTags = userTagRepository.saveAll(userTags);
        savedUser.setUserTags(new HashSet<>(savedUserTags));

        Set<UserZone> userZones = zoneList.subList(from, to).stream()
                .map(zone -> UserZone.builder()
                        .user(savedUser)
                        .zone(zone)
                        .build())
                .collect(Collectors.toSet());
        List<UserZone> savedUserZones = userZoneRepository.saveAll(userZones);
        savedUser.setUserZones(new HashSet<>(savedUserZones));

        return savedUser;
    }
}