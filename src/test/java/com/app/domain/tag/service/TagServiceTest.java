package com.app.domain.tag.service;

import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.global.error.exception.InvalidTagException;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.app.TestUtils.createTags;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.groups.Tuple.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class TagServiceTest {

    @Autowired
    TagService tagService;

    @Autowired
    TagRepository tagRepository;

    @Test
    void initTagData() {
        // when
        tagService.initTagData();
        List<Tag> tags = tagRepository.findAll();

        // then
        assertThat(tags).hasSize(8)
                .extracting("title")
                .containsExactlyInAnyOrder("JavaScript", "Vue.js", "React", "Node.js", "CSS", "HTML", "Spring", "Java");
    }

    @Test
    void findTags() {
        // given
        List<Tag> savedTags = tagRepository.saveAll(createTags(3));
        Set<String> tags = savedTags.stream().map(Tag::getTitle).collect(Collectors.toSet());

        // when
        Set<Tag> tagSet = tagService.findTags(tags);

        // then
        assertThat(tagSet).hasSize(3)
                .extracting("title")
                .containsExactlyInAnyOrder("tag1", "tag2", "tag3");
    }

    @Test
    void findTags_with_wrong_input() {
        // given
        List<Tag> savedTags = tagRepository.saveAll(createTags(3));

        // expected
        assertThatThrownBy(() -> tagService.findTags(Set.of("태그1", "태그2"))).isInstanceOf(InvalidTagException.class);

    }

    @Test
    void getTags() {
        // given
        List<Tag> savedTags = tagRepository.saveAll(createTags(3));

        // when
        Set<Tag> tagSet = tagService.getTags();

        // then
        assertThat(tagSet).hasSize(3)
                .extracting("title")
                .containsExactlyInAnyOrder("tag1", "tag2", "tag3");
    }

    @Test
    void saveAll() {
        // given
        List<Tag> tagList = createTags(3);

        // when
        tagService.saveAll(tagList);
        List<Tag> tags = tagRepository.findAll();

        // then
        assertThat(tags).hasSize(3);
    }

    @Test
    void findByTitle() {
        //given
        tagRepository.saveAll(createTags(5));

        // when
        Tag tag = tagService.findByTitle("tag1");

        // then
        assertThat(tag).isNotNull();
        assertThat(tag.getId()).isNotNull();
        assertThat(tag.getTitle()).isEqualTo("tag1");
    }

    @Test
    void findByTitle_wrong_input() {
        //given
        tagRepository.saveAll(createTags(5));

        // when
        Tag tag = tagService.findByTitle("tag1");

        // then
        assertThatThrownBy(() -> tagService.findByTitle("tagtag1")).isInstanceOf(InvalidTagException.class);
    }

    @Test
    void findAll() {
        // given
        List<Tag> tagList = createTags(3);
        tagRepository.saveAll(tagList);

        // when
        List<Tag> tags = tagService.findAll();

        // then
        assertThat(tags).hasSize(3);
    }
}