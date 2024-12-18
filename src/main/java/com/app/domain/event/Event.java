package com.app.domain.event;

import com.app.domain.study.Study;
import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Study study;

    @Column(nullable = false)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false)
    private LocalDateTime endEnrollmentDateTime;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    @Column
    private Integer limitOfEnrollments;

    @OneToMany(mappedBy = "event")
    @OrderBy("enrolledAt")
    private List<Enrollment> enrollments = new ArrayList<>();

    @Builder
    public Event(Study study, String title, String description, LocalDateTime endEnrollmentDateTime,
                 LocalDateTime startDateTime, LocalDateTime endDateTime, Integer limitOfEnrollments,
                 List<Enrollment> enrollments) {
        this.study = study;
        this.title = title;
        this.description = description;
        this.endEnrollmentDateTime = endEnrollmentDateTime;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.limitOfEnrollments = limitOfEnrollments;
        this.enrollments = enrollments != null ? enrollments : new ArrayList<>();
    }

    public boolean isEnrollableFor(User user) {
        return isNotClosed() && !isAttended(user) && !isAlreadyEnrolled(user);
    }

    public boolean isDisenrollableFor(User user) {
        return isNotClosed() && !isAttended(user) && isAlreadyEnrolled(user);
    }

    private boolean isNotClosed() {
        return this.endEnrollmentDateTime.isAfter(LocalDateTime.now());
    }

    public boolean isAttended(User user) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.isEnrolledByUser(user) && enrollment.isAttended()) {
                return true;
            }
        }
        return false;
    }

    private boolean isAlreadyEnrolled(User user) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment.isEnrolledByUser(user)) {
                return true;
            }
        }
        return false;
    }

    public boolean canAccept(Enrollment enrollment) {
        return this.enrollments.contains(enrollment)
                && canAcceptMoreEnrollments()
                && !enrollment.isAttended()
                && !enrollment.isAccepted();
    }

    public boolean canReject(Enrollment enrollment) {
        return this.enrollments.contains(enrollment)
                && !enrollment.isAttended()
                && enrollment.isAccepted();
    }

    private long getNumberOfAcceptedEnrollments() {
        return this.enrollments != null ? this.enrollments.stream().filter(Enrollment::isAccepted).count() : 0L;
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setEvent(this);
    }

    // TODO: enrollment의 event가 null이 되면 삭제시키는게 나을지도
    public void removeEnrollment(Enrollment enrollment) {
        this.enrollments.remove(enrollment);
        enrollment.setEvent(null);
    }

    public void accept(Enrollment enrollment) {
        if (canAcceptMoreEnrollments()) {
            enrollment.setAccepted(true);
        }
    }

    public void reject(Enrollment enrollment) {
        enrollment.setAccepted(false);
    }

    private boolean canAcceptMoreEnrollments() {
        return this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
    }

    public EventEditor.EventEditorBuilder toEditor() {
        return EventEditor.builder()
                .title(title)
                .description(description)
                .endEnrollmentDateTime(endEnrollmentDateTime)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime);
    }

    public void editEvent(EventEditor eventEditor) {
        this.title = eventEditor.getTitle();
        this.description = eventEditor.getDescription();
        this.endEnrollmentDateTime = eventEditor.getEndEnrollmentDateTime();
        this.startDateTime = eventEditor.getStartDateTime();
        this.endDateTime = eventEditor.getEndDateTime();
        this.limitOfEnrollments = eventEditor.getLimitOfEnrollments();
    }

    public boolean isAbleToAccept() {
        return this.limitOfEnrollments > this.getNumberOfAcceptedEnrollments();
    }

}
