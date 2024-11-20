package com.app.domain.user;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.user.constant.Role;
import com.app.domain.userTag.UserTag;
import com.app.domain.userZone.UserZone;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "users")
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserTag> userTags = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final Set<UserZone> userZones = new HashSet<>();

    @Builder
    public User(String name, String email, String picture, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public void setUserZones(Set<UserZone> userZones) {
        this.userZones.clear();
        this.userZones.addAll(userZones);
    }
    
    // final 없애고 새 set으로 바꾸면 JPA 호환성 이슈있을수도 있음
    public void setUserTags(Set<UserTag> userTags) {
        this.userTags.clear();
        this.userTags.addAll(userTags);
    }
}

