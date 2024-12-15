package com.app.domain.study;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.study.studyManager.StudyManager;
import com.app.domain.study.studyMember.StudyMember;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.user.User;
import com.app.global.error.exception.InvalidRecruitmentStateException;
import com.app.global.error.exception.InvalidStudyJoinConditionException;
import com.app.global.error.exception.InvalidStudyPublishStateException;
import com.app.global.error.exception.StudyCloseException;
import jakarta.persistence.*;
import lombok.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private int memberCount;

    @Builder
    public Study(String path, String title, String shortDescription, String fullDescription, String image) {
        this.path = path;
        this.title = title;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.image = image;
    }

    public void publish() {
        if (!this.closed && !this.published) {
            this.published = true;
            this.publishedDateTime = LocalDateTime.now();
        } else {
            throw new InvalidStudyPublishStateException();
        }
    }

    public void close() {
        if (this.published && !this.closed) {
            this.closed = true;
            this.closedDateTime = LocalDateTime.now();
        } else {
            throw new StudyCloseException();
        }
    }

    public void addManager(StudyManager manager) {
        this.studyManagers.add(manager);
    }

    public void addMember(StudyMember member) {
        if (!isJoinable(member.getUser())) {
            throw new InvalidStudyJoinConditionException();
        }
            this.studyMembers.add(member);
            this.memberCount++;
    }

    public void removeMember(StudyMember member) {
        this.studyMembers.remove(member);
        this.memberCount--;
        member.disconnectStudy();
    }

    public boolean isMember(User user) {
        return this.studyMembers.stream()
                .anyMatch(studyMember -> studyMember.isSameUser(user));
    }

    public boolean isManager(User user) {
        return this.studyManagers.stream()
                .anyMatch(studyManager -> studyManager.isSameUser(user));
    }

    public boolean isJoinable(User user) {
        return this.isPublished() && !isMember(user) && !isManager(user);
    }

    public void addStudyTags(Set<StudyTag> tagsToAdd) {
        this.studyTags.addAll(tagsToAdd);
    }

    public void addStudyZones(Set<StudyZone> zonesToAdd) {
        this.studyZones.addAll(zonesToAdd);
    }

    public void removeStudyTags(Set<StudyTag> tagsToRemove) {
        this.studyTags.removeAll(tagsToRemove);
        tagsToRemove.forEach(StudyTag::disconnectStudy);
    }

    public void removeStudyZones(Set<StudyZone> zonesToRemove) {
        this.studyZones.removeAll(zonesToRemove);
        zonesToRemove.forEach(StudyZone::disconnectStudy);
    }

    public StudyEditor.StudyEditorBuilder toEditor() {
        return StudyEditor.builder()
                .path(path)
                .title(title)
                .shortDescription(shortDescription)
                .fullDescription(fullDescription)
                .image(image);
    }

    public void edit(StudyEditor studyEditor) {
        this.path = studyEditor.getPath();
        this.title = studyEditor.getTitle();
        this.shortDescription = studyEditor.getShortDescription();
        this.fullDescription = studyEditor.getFullDescription();
        this.image = studyEditor.getImage();
    }

    public void startRecruit(LocalDateTime currentTime) {
        if (this.recruiting) {
            throw new InvalidRecruitmentStateException();
        }

        if (canUpdateRecruiting(currentTime)) {
            this.recruiting = true;
            this.recruitingUpdatedDateTime = currentTime;
        } else {
            throw new InvalidRecruitmentStateException();
        }
    }

    public void stopRecruit(LocalDateTime currentTime) {
        if (!this.recruiting) {
            throw new InvalidRecruitmentStateException();
        }

        if (canUpdateRecruiting(currentTime)) {
            this.recruiting = false;
            this.recruitingUpdatedDateTime = currentTime;
        } else {
            throw new InvalidRecruitmentStateException();
        }
    }

    private boolean canUpdateRecruiting(LocalDateTime currentTime) {
        return this.published &&
                !this.closed &&
                (this.recruitingUpdatedDateTime == null || this.recruitingUpdatedDateTime.isBefore(currentTime.minusHours(1)));
    }

    public String getEncodedPath() {
        return URLEncoder.encode(this.path, StandardCharsets.UTF_8);
    }
}
