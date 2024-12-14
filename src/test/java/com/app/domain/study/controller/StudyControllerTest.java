package com.app.domain.study.controller;

import com.app.WithAccount;
import com.app.domain.study.Study;
import com.app.domain.study.dto.StudyCreateRequest;
import com.app.domain.study.dto.StudyUpdateRequest;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyManager.repository.StudyManagerRepository;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyMember.repository.StudyMemberRepository;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyTag.repository.StudyTagRepository;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.study.studyZone.repository.StudyZoneRepository;
import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.user.User;
import com.app.domain.user.constant.Role;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.userTag.UserTag;
import com.app.domain.userTag.repository.UserTagRepository;
import com.app.domain.userZone.UserZone;
import com.app.domain.userZone.repository.UserZoneRepository;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.InvalidTagException;
import com.app.global.error.exception.StudyNotFoundException;
import com.app.global.error.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class StudyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudyRepository studyRepository;

    @Autowired
    StudyManagerRepository studyManagerRepository;

    @Autowired
    StudyMemberRepository studyMemberRepository;

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    StudyZoneRepository studyZoneRepository;

    @Autowired
    StudyTagRepository studyTagRepository;
    @Autowired
    private UserTagRepository userTagRepository;
    @Autowired
    private UserZoneRepository userZoneRepository;

    @Test
    @WithAccount
    @DisplayName("스터디 생성 테스트")
    void create_study_with_correct_input() throws Exception {
        // given
        StudyCreateRequest request = StudyCreateRequest.builder()
                .path("test1")
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value("test1"))
                .andExpect(jsonPath("$.title").value("테스트 스터디1"))
                .andExpect(jsonPath("$.shortDescription").value("짧은 글 설명1"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("path는 최소 두 글자 이상. 아닐시 예외 발생")
    void create_study_with_wrong_path() throws Exception {
        // given
        StudyCreateRequest request = StudyCreateRequest.builder()
                .path("t")
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("400 BAD_REQUEST"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("title은 최대 15글자. 아닐시 예외 발생")
    void create_study_with_wrong_title() throws Exception {
        // given
        StudyCreateRequest request = StudyCreateRequest.builder()
                .path("test1")
                .title("1234567890124567")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value("400 BAD_REQUEST"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("스터디 조회 성공테스트")
    void find_study_with_correct_path() throws Exception {
        // given
        // study 세팅
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager);
        study.addManager(manager);
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/{path}", path))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.path").value(path))
                .andExpect(jsonPath("$.title").value("테스트 스터디1"))
                .andExpect(jsonPath("$.shortDescription").value("짧은 글 설명1"))
                .andExpect(jsonPath("$.fullDescription").value("긴 글 설명1"))
                .andExpect(jsonPath("$.published").value(false))
                .andExpect(jsonPath("$.closed").value(false))
                .andExpect(jsonPath("$.recruiting").value(false))
                .andExpect(jsonPath("$.tags").isEmpty())
                .andExpect(jsonPath("$.zones").isEmpty())
                .andExpect(jsonPath("$.members").isEmpty())
                .andExpect(jsonPath("$.managers[0].name").value("testName"))
                .andExpect(jsonPath("$.managers[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$.isMember").value(false))
                .andExpect(jsonPath("$.isJoinable").value(false))
                .andExpect(jsonPath("$.isManager").value(true))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 path로 스터디 조회 요청시 예외 발생")
    void find_study_with_wrong_path() throws Exception {
        // given
        // study 세팅
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager);
        study.addManager(manager);
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/{path}", "wrong"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("전체 스터디 목록 조회 성공 테스트")
    void find_studies_with_correct_input() throws Exception {
        // given
        // 스터디 세팅
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        for (int i = 1; i <= 20; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            StudyManager manager = StudyManager.createManager(user, study);
            studyManagerRepository.save(manager);
            study.addManager(manager);
            studyRepository.save(study);
        }

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(9))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalCount").value(20))
                .andExpect(jsonPath("$.size").value(9))
                .andExpect(jsonPath("$.content[0].path").value("path1"))
                .andExpect(jsonPath("$.content[0].title").value("테스트 스터디1"))
                .andExpect(jsonPath("$.content[0].shortDescription").value("짧은 글 설명1"))
                .andExpect(jsonPath("$.content[8].path").value("path9"))
                .andExpect(jsonPath("$.content[8].title").value("테스트 스터디9"))
                .andExpect(jsonPath("$.content[8].shortDescription").value("짧은 글 설명9"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount(name = "멤버1", email = "member@example.com", role = "ROLE_GUEST")
    @DisplayName("가입 스터디 목록 조회 성공 테스트")
    void find_joined_studies_with_correct_input() throws Exception {
        // given
        // 스터디 세팅
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.GUEST)
                .build();
        User savedUser = userRepository.save(user);
        for (int i = 1; i <= 20; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            StudyManager manager = StudyManager.createManager(savedUser, study);
            studyManagerRepository.save(manager);
            study.addManager(manager);
            studyRepository.save(study);
        }

        User findMember = userRepository.findByEmail("member@example.com").orElseThrow(UserNotFoundException::new);

        // 스터디 가입 세팅
        for (int i = 1; i <= 3; i++) {
            Study study = studyRepository.findByPath("path" + i).orElseThrow(StudyNotFoundException::new);
            study.publish();

            StudyMember savedStudyMember = studyMemberRepository.save(StudyMember.createMember(findMember, study));
            study.addMember(savedStudyMember);
        }


        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/my-joined-study-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.totalCount").value(3))
                .andExpect(jsonPath("$.size").value(9))
                .andExpect(jsonPath("$.content[0].path").value("path1"))
                .andExpect(jsonPath("$.content[0].title").value("테스트 스터디1"))
                .andExpect(jsonPath("$.content[0].shortDescription").value("짧은 글 설명1"))
                .andExpect(jsonPath("$.content[2].path").value("path3"))
                .andExpect(jsonPath("$.content[2].title").value("테스트 스터디3"))
                .andExpect(jsonPath("$.content[2].shortDescription").value("짧은 글 설명3"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("관리 스터디 목록 조회 성공 테스트")
    void find_managed_studies_with_correct_input() throws Exception {
        // given
        // 스터디 세팅
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        for (int i = 1; i <= 20; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            StudyManager manager = StudyManager.createManager(user, study);
            studyManagerRepository.save(manager);
            study.addManager(manager);
            studyRepository.save(study);
        }

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/my-managed-study-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(9))
                .andExpect(jsonPath("$.currentPage").value(1))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.totalCount").value(20))
                .andExpect(jsonPath("$.size").value(9))
                .andExpect(jsonPath("$.content[0].path").value("path1"))
                .andExpect(jsonPath("$.content[0].title").value("테스트 스터디1"))
                .andExpect(jsonPath("$.content[0].shortDescription").value("짧은 글 설명1"))
                .andExpect(jsonPath("$.content[8].path").value("path9"))
                .andExpect(jsonPath("$.content[8].title").value("테스트 스터디9"))
                .andExpect(jsonPath("$.content[8].shortDescription").value("짧은 글 설명9"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("study wishlist 성공 테스트")
    void find_study_wishlist() throws Exception {
        // given
        // given
        // 스터디 세팅 -> 태그 지역도 세팅같이
        User user = User.builder()
                .name("관리자")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();
        User savedUser = userRepository.save(user);

        for (int i = 1; i <= 20; i++) {
            Study study = Study.builder()
                    .path("path" + i)
                    .title("테스트 스터디" + i)
                    .shortDescription("짧은 글 설명" + i)
                    .fullDescription("긴 글 설명" + i)
                    .build();
            studyRepository.save(study);

            StudyManager manager = StudyManager.createManager(savedUser, study);
            studyManagerRepository.save(manager);
            study.addManager(manager);

            // studyZone 세팅
            Zone zone = Zone.builder()
                    .city("city" + i)
                    .localName("localName" + i)
                    .province("province" + i)
                    .build();
            Zone savedZone = zoneRepository.save(zone);

            StudyZone studyZone = StudyZone.builder()
                    .study(study)
                    .zone(savedZone)
                    .build();
            StudyZone savedStudyZone = studyZoneRepository.save(studyZone);
            study.addStudyZones(Set.of(savedStudyZone));

            // studyTag 세팅
            Tag tag = Tag.builder()
                    .title("tag" + i)
                    .build();
            Tag savedTag = tagRepository.save(tag);

            StudyTag studyTag = StudyTag.builder()
                    .study(study)
                    .tag(savedTag)
                    .build();
            StudyTag savedStudyTag = studyTagRepository.save(studyTag);
            study.addStudyTags(Set.of(savedStudyTag));

        }

        // 유저 세팅 -> 태그, 지역
        User guest = userRepository.findByEmail("guest@gmail.com").orElseThrow(UserNotFoundException::new);

        // 유저 태그 세팅
        List<UserTag> userTags = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            Tag tag = tagRepository.findByTitle("tag" + i).orElseThrow(InvalidTagException::new);
            UserTag userTag = UserTag.builder()
                    .user(guest)
                    .tag(tag)
                    .build();
           userTags.add(userTag);
        }
        List<UserTag> savedUserTags = userTagRepository.saveAll(userTags);
        user.setUserTags(new HashSet<>(savedUserTags));
        
        // 유저 지역 세팅
        List<Zone> zones = zoneRepository.findAll().subList(0, 2);
        System.out.println(" zone size: " + zones.size());
        zones.forEach(zone -> {
            System.out.println("Zone: ID=" + zone.getId() + ", City=" + zone.getCity());
        });

        List<UserZone> userZones = new ArrayList<>();
        for (Zone zone : zones) {
            UserZone userZone = UserZone.builder()
                    .user(guest)
                    .zone(zone)
                    .build();
            userZones.add(userZone);
        }
        List<UserZone> savedUserZones = userZoneRepository.saveAll(userZones);

        savedUserZones.forEach(userZone -> {
            System.out.println("UserZone: User=" + userZone.getUser().getEmail() +
                    ", Zone ID=" + userZone.getZone().getId());
        });

        user.setUserZones(new HashSet<>(savedUserZones));

        // expected
        mockMvc.perform(MockMvcRequestBuilders.get("/study/my-study-wishlist"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].path").value("path1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].title").value("테스트 스터디1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].shortDescription").value("짧은 글 설명2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].tags[0].tag").value("tag2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].zones[0].city").value("city2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPage").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.totalCount").value(2))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("스터디 소개 수정 성공 테스트")
    void update_study_description_with_correct_input() throws Exception {
        // given
        // study 세팅
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager savedManager = studyManagerRepository.save(StudyManager.createManager(user, study));
        study.addManager(savedManager);

        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .shortDescription("수정된 짧은 글1")
                .fullDescription("수정된 긴 글1")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study/description/{path}", path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.path").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("테스트 스터디1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.shortDescription").value("수정된 짧은 글1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullDescription").value("수정된 긴 글1"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount
    @DisplayName("잘못된 path로 스터디 조회 요청시 예외 발생")
    void update_study_description_with_wrong_path() throws Exception {
        // given
        // study 세팅
        String path = "test";
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager = StudyManager.createManager(user, study);
        studyManagerRepository.save(manager);
        study.addManager(manager);
        Study savedStudy = studyRepository.save(study);

        StudyUpdateRequest request = StudyUpdateRequest.builder()
                .shortDescription("수정된 짧은 글1")
                .fullDescription("수정된 긴 글 1")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/study/description/{path}", "wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("스터디 조인 성공 테스트")
    void join_study_with_correct_input() throws Exception {
        // given
        // study 세팅
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.GUEST)
                .build();
        User savedUser = userRepository.save(user);

        String path = "test";
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(savedUser, study);
        StudyManager savedManager = studyManagerRepository.save(manager);
        study.addManager(savedManager);

        savedStudy.publish();

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/join", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.members[0].name").value("guest"))
                .andExpect(jsonPath("$.members[0].email").value("guest@gmail.com"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("잘못된 path로 스터디 조인시 예외 발생")
    void join_study_with_wrong_path() throws Exception {
        // given
        // study 세팅
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.GUEST)
                .build();
        User savedUser = userRepository.save(user);

        String path = "test";
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager = StudyManager.createManager(savedUser, study);
        StudyManager savedManager
                = studyManagerRepository.save(manager);
        study.addManager(savedManager);
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/join", "wrong"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("스터디 publish 전에 스터디 조인시 예외 발생")
    void join_study_before_study_published() throws Exception {
        // given
        // study 세팅
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.GUEST)
                .build();
        User savedUser = userRepository.save(user);

        String path = "test";
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        StudyManager manager = StudyManager.createManager(savedUser, study);
        StudyManager savedManager = studyManagerRepository.save(manager);
        study.addManager(savedManager);
        Study savedStudy = studyRepository.save(study);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/join", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_STUDY_JOIN_CONDITION.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_STUDY_JOIN_CONDITION.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("이미 스터디 멤버이면 스터디 조인시 예외 발생")
    void join_study_with_already_study_member() throws Exception {
        // given
        // study 세팅
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.GUEST)
                .build();
        User savedUser = userRepository.save(user);

        String path = "test";
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);
        StudyManager manager = StudyManager.createManager(savedUser, savedStudy);
        StudyManager savedManager = studyManagerRepository.save(manager);
        savedStudy.addManager(savedManager);
        savedStudy.publish();

        // 스터디 가입
        User guest = userRepository.findByEmail("guest@gmail.com").orElseThrow(UserNotFoundException::new);

        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);
        savedStudy.addMember(savedMember);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/join", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_STUDY_JOIN_CONDITION.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_STUDY_JOIN_CONDITION.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount()
    @DisplayName("스터디 매니저이면 스터디 조인시 예외 발생")
    void join_study_with_study_manager() throws Exception {
        // given
        // study 세팅
        User user = userRepository.findByEmail("test@gmail.com").orElseThrow(UserNotFoundException::new);

        String path = "test";
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);
        StudyManager manager = StudyManager.createManager(user, savedStudy);
        StudyManager savedManager = studyManagerRepository.save(manager);
        savedStudy.addManager(savedManager);
        savedStudy.publish();

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/join", path))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.INVALID_STUDY_JOIN_CONDITION.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.INVALID_STUDY_JOIN_CONDITION.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("스터디 탈퇴 성공 테스트")
    void leave_study_with_correct_input() throws Exception {
        // given
        // 스터디 세팅
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.GUEST)
                .build();
        User savedUser = userRepository.save(user);

        String path = "test";
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(savedUser, study);
        StudyManager savedManager = studyManagerRepository.save(manager);
        study.addManager(savedManager);

        savedStudy.publish();

        // 스터디 가입
        User guest = userRepository.findByEmail("guest@gmail.com").orElseThrow(UserNotFoundException::new);

        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);
        savedStudy.addMember(savedMember);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/leave", path))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.members[*]", Matchers.not(Matchers.hasItem("guest@gmail.com"))))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @WithAccount(name = "guest", email = "guest@gmail.com", role = "ROLE_GUEST")
    @DisplayName("잘못된 path로 스터디 탈퇴 시도하면 예외")
    void leave_study_with_wrong_path() throws Exception {
        // given
        // 스터디 세팅
        User user = User.builder()
                .name("testName")
                .email("test@gmail.com")
                .role(Role.GUEST)
                .build();
        User savedUser = userRepository.save(user);

        String path = "test";
        Study study = Study.builder()
                .path(path)
                .title("테스트 스터디1")
                .shortDescription("짧은 글 설명1")
                .fullDescription("긴 글 설명1")
                .build();
        Study savedStudy = studyRepository.save(study);

        StudyManager manager = StudyManager.createManager(savedUser, study);
        StudyManager savedManager = studyManagerRepository.save(manager);
        study.addManager(savedManager);

        savedStudy.publish();

        // 스터디 가입
        User guest = userRepository.findByEmail("guest@gmail.com").orElseThrow(UserNotFoundException::new);

        StudyMember studyMember = StudyMember.createMember(guest, study);
        StudyMember savedMember = studyMemberRepository.save(studyMember);
        savedStudy.addMember(savedMember);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/study/{path}/leave", "wrong"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getErrorCode()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorMessage")
                        .value(ErrorCode.STUDY_NOT_EXISTS.getMessage()))
                .andDo(MockMvcResultHandlers.print());
    }
}