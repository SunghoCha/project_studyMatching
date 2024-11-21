package com.app.domain.study.repository;

import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.dto.StudyQueryResponse;
import com.app.domain.study.dto.StudySearchCond;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyTag.repository.StudyTagRepository;
import com.app.domain.tag.Tag;
import com.app.domain.tag.dto.TagResponse;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.exception.InvalidTagException;
import com.app.global.error.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@Rollback(value = false)
class StudyQueryRepositoryTest {

    @Autowired
    StudyQueryRepository queryRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyTagRepository studyTagRepository;

    @Autowired
    private StudyQueryRepository studyQueryRepository;

    @BeforeEach
    void setup() {
        //tagRepository.deleteAll();
    }

    @Test
    @WithAccount
    @DisplayName("")
    void test1() {
        // given
        // TODO 미완성 로직
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        List<Tag> tagList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Tag tag = new Tag("Spring" + i);
            tagList.add(tag);
        }
        tagRepository.saveAll(tagList);


        for (int i = 1; i <= 10; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .shortDescription("test" + i)
                    .build();
            studyRepository.save(study);

            StudyTag studyTag = StudyTag.builder()
                    .study(study)
                    .tag(tagRepository.findByTitle("Spring" + i).orElseThrow(InvalidTagException::new))
                    .build();
            studyTagRepository.save(studyTag);

            study.addStudyTags(Set.of(studyTag));

        }
        List<Study> studyList = studyRepository.findAll();
//        System.out.println(" ==================================================");
//        for (Study study : studyList) {
//            study.getStudyTags().forEach((studyTag) -> {
//                    System.out.println(studyTag.getTag().getTitle());
//                    System.out.println(studyTag.getStudy().getId());
//            });
//        }
//        System.out.println(" ==================================================");


        StudySearchCond searchCond = StudySearchCond.builder()
                .tags(List.of("Spring1"))
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<StudyQueryResponse> studyQueryResponses = queryRepository.searchStudies(searchCond, pageRequest);
        System.out.println("studyQueryResponses.size() = " + studyQueryResponses.size());
        for (StudyQueryResponse studyQueryResponse : studyQueryResponses) {
            List<TagResponse> tags = studyQueryResponse.getTags();
            System.out.println("studyQueryResponse.getTags() = " + tags);
            for (TagResponse tag : tags) {
                System.out.println("tag.getTag() = " + tag.getTag());
            }
            // when

            // then
        }
    }

    @Test
    @WithAccount
    @DisplayName("test")
    void test2() {
        // given
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        List<Tag> tagList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Tag tag = new Tag("Spring" + i);
            tagList.add(tag);
        }
        tagRepository.saveAll(tagList);


        for (int i = 1; i <= 10; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .shortDescription("test" + i)
                    .build();
            studyRepository.save(study);

            Tag tag = tagRepository.findByTitle("Spring" + i).orElseThrow(InvalidTagException::new);
            StudyTag studyTag = StudyTag.builder()
                    .study(study)
                    .tag(tag)
                    .build();

            // 명시적으로 StudyTag 저장
            studyTagRepository.save(studyTag);
            study.addStudyTags(Set.of(studyTag)); // Study와 StudyTag 연결

        }

        List<Study> studyList = studyRepository.findAll();
        System.out.println(" ==================================================");
        for (Study study : studyList) {
            study.getStudyTags().forEach((studyTag) -> {
                System.out.println(studyTag.getTag().getTitle());
                System.out.println(studyTag.getStudy().getId());
            });
        }
        System.out.println(" ==================================================");


        List<StudyQueryResponse> studies = studyQueryRepository.searchStudies3("Spring1");
        System.out.println("studies** = " + studies);
        for (StudyQueryResponse study : studies) {
            System.out.println("$$$$$$$$$$$$$$ = " + study.getTitle());
            // when

            // then
        }
        // when

        // then
    }

    @Test
    @DisplayName("")
    void test3() {
        // given
        // TODO 미완성 로직
        PageRequest pageRequest = PageRequest.of(1, 10);

        List<Study> allStudies = queryRepository.findAllStudies(pageRequest);
        // when

        // then
    }

}