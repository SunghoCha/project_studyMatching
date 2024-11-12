package com.app.domain.study;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "study")
    private Set<StudyManager> studyManagers = new HashSet<>();

    @OneToMany(mappedBy = "study")
    private Set<StudyMember> studyMembers = new HashSet<>();

    @OneToMany(mappedBy = "study")
    private Set<StudyTag> studyTags = new HashSet<>();

    @OneToMany(mappedBy = "study")
    private Set<StudyZone> studyZones = new HashSet<>();

    @Column(unique = true, nullable = false)
    private String path;

    private String title;

    private String shortDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER) // 항상 같이 로딩할 듯
    private String fullDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;

    @Builder
    public Study(String path, String title, String shortDescription, String fullDescription, String image) {
        this.path = path;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.image = image;
    }

    public void addManager(User user) {
        StudyManager manager = StudyManager.createManager(user, this);
        this.studyManagers.add(manager);
    }
}
