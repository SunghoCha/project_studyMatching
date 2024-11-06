package com.app.domain.userTag;

import com.app.domain.tag.Tag;
import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Entity
@Getter
public class UserTag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tag_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    private UserTag(User user, Tag tag) {
        this.user = user;
        this.tag = tag;
    }
}
