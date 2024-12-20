package com.app.domain.study;

import com.app.TestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;

import static com.app.TestUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class StudyTest {

    @Test
    void publish() {
        // given
        Study study = Study.builder()
                .path("test")
                .title("제목")
                .build();

        // when
        study.publish(Clock.systemDefaultZone());

        // then
        Assertions.assertThat(study.isPublished()).isTrue();
    }

    @Test
    void close() {
        // given
        Study study = Study.builder()
                .path("test")
                .title("제목")
                .build();

        // when
        study.publish(Clock.systemDefaultZone());
        study.close(Clock.systemDefaultZone());

        // then
        Assertions.assertThat(study.isClosed()).isTrue();
    }

    @Test
    void addManager() {
    }

    @Test
    void addMember() {
    }

    @Test
    void removeMember() {
    }

    @Test
    void isMember() {
    }

    @Test
    void isManager() {
    }

    @Test
    void isJoinable() {
    }

    @Test
    void addStudyTags() {
    }

    @Test
    void addStudyZones() {
    }

    @Test
    void removeStudyTags() {
    }

    @Test
    void removeStudyZones() {
    }

    @Test
    void toEditor() {
    }

    @Test
    void edit() {
    }

    @Test
    void startRecruit() {
    }

    @Test
    void stopRecruit() {
    }

    @Test
    void testEquals() {
    }

    @Test
    void canEqual() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void isRecruiting() {
    }

    @Test
    void isPublished() {
    }

    @Test
    void isClosed() {
    }
}