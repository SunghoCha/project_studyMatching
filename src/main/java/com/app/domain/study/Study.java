package com.app.domain.study;

import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyZone.StudyZone;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Study {

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

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER) // 항상 같이 로딩할 듯
    private String fullDescription;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    private String image;


}
