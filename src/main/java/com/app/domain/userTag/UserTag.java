package com.app.domain.userTag;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.tag.Tag;
import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"user", "tag"}, callSuper = false)
public class UserTag extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_tag_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Builder
    private UserTag(User user, Tag tag) {
        this.user = user;
        this.tag = tag;
    }
}
