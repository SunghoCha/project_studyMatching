package com.app.domain.tag.service;

import com.app.domain.tag.Tag;
import com.app.domain.tag.repository.TagRepository;
import com.app.domain.user.repository.UserRepository;
import com.app.global.error.exception.InvalidTagException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public void initTagData() {
        List<Tag> tags = List.of(new Tag("JavaScript"),
                new Tag("Vue.js"),
                new Tag("React"),
                new Tag("Node.js"),
                new Tag("CSS"),
                new Tag("HTML"),
                new Tag("Spring"),
                new Tag("Java"));
        tagRepository.saveAll(tags);
    }

    public Set<Tag> findTags(Set<String> tagTitles) {
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);
        validate(tagTitles);

        return tags;
    }

    private void validate(Set<String> tagTitles) {
        Set<Tag> tags = tagRepository.findByTitleIn(tagTitles);
        if (tagTitles.size() != tags.size()) {
            throw new InvalidTagException();
        };
    }

    public Set<Tag> getTags() {
        return new HashSet<>(tagRepository.findAll());
    }

    public void saveAll(List<Tag> tags) {
        tagRepository.saveAll(new HashSet<Tag>(tags));
    }

    public Tag findByTitle(String title) {
        return tagRepository.findByTitle(title).orElseThrow(InvalidTagException::new);
    }

    public List<Tag> findAll() {
        return tagRepository.findAll();
    }
}
