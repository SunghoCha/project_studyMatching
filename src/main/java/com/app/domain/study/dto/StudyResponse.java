package com.app.domain.study.dto;

import com.app.domain.study.Study;
import com.app.domain.tag.dto.TagResponse;
import com.app.domain.user.User;
import com.app.domain.user.dto.UserResponse;
import com.app.domain.zone.dto.ZoneResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class StudyResponse {

    private String path;
    private String title;
    private boolean published;
    private boolean closed;
    private boolean recruiting;
    private String shortDescription;
    private String fullDescription;
    private List<TagResponse> tags;
    private List<ZoneResponse> zones;
    private List<UserResponse> members;
    private List<UserResponse> managers;

    @JsonProperty("isJoinable")
    private boolean isJoinable;

    @JsonProperty("isMember")
    private boolean isMember;

    @JsonProperty("isManager")
    private boolean isManager;

    @Builder
    private StudyResponse(String path, String title, boolean published, boolean closed,
                          boolean recruiting, String shortDescription, String fullDescription,
                          List<TagResponse> tags, List<ZoneResponse> zones, List<UserResponse> members,
                          List<UserResponse> managers, boolean isJoinable, boolean isMember, boolean isManager) {
        this.path = path;
        this.title = title;
        this.published = published;
        this.closed = closed;
        this.recruiting = recruiting;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.tags = tags;
        this.zones = zones;
        this.members = members;
        this.managers = managers;
        this.isJoinable = isJoinable;
        this.isMember = isMember;
        this.isManager = isManager;
    }

    public static StudyResponse of(User user, Study study) {
        // TODO: study 쿼리 최적화
        return StudyResponse.builder()
                .path(study.getPath())
                .title(study.getTitle())
                .published(study.isPublished())
                .closed(study.isClosed())
                .recruiting(study.isRecruiting())
                .shortDescription(study.getShortDescription())
                .fullDescription(study.getFullDescription())
                .tags(study.getStudyTags().stream()
                        .map(studyTag -> TagResponse.of(studyTag.getTag()))
                        .toList())
                .zones(study.getStudyZones().stream()
                        .map(studyZone -> ZoneResponse.of(studyZone.getZone()))
                        .toList())
                .members(study.getStudyMembers().stream()
                        .map(studyMember -> UserResponse.of(studyMember.getUser()))
                        .toList())
                .managers(study.getStudyManagers().stream()
                        .map(studyManager -> UserResponse.of(studyManager.getUser()))
                        .toList())
                .isJoinable(study.isJoinable(user))
                .isMember(study.isMember(user))
                .isManager(study.isManager(user))
                .build();
    }

    public static StudyResponse of(Study study) {
        return StudyResponse.builder()
                .path(study.getPath())
                .title(study.getTitle())
                .shortDescription(study.getShortDescription())
                .tags(study.getStudyTags().stream()
                        .map(studyTag -> TagResponse.of(studyTag.getTag()))
                        .toList())
                .zones(study.getStudyZones().stream()
                        .map(studyZone -> ZoneResponse.of(studyZone.getZone()))
                        .toList())
                .build();
    }
}
