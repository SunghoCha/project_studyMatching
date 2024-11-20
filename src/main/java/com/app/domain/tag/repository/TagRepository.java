package com.app.domain.tag.repository;

import com.app.domain.tag.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Set<Tag> findByTitleIn(Set<String> userTags);

    Optional<Tag> findByTitle(String title);
}
