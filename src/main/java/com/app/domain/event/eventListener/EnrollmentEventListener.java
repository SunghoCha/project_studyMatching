package com.app.domain.event.eventListener;

import com.app.domain.event.Enrollment;
import com.app.domain.event.Event;
import com.app.domain.event.eventListener.event.EnrollmentEvent;
import com.app.domain.notification.Notification;
import com.app.domain.notification.NotificationType;
import com.app.domain.notification.repository.NotificationRepository;
import com.app.domain.study.Study;
import com.app.domain.user.User;
import com.app.global.config.properties.AppProperties;
import com.app.infra.email.EmailMessage;
import com.app.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Async
@Component
@RequiredArgsConstructor
@Transactional
public class EnrollmentEventListener {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleEnrollmentEvent(EnrollmentEvent enrollmentEvent) {
        Enrollment enrollment = enrollmentEvent.getEnrollment();
        User user = enrollment.getUser();
        Event event = enrollment.getEvent();
        Study study = event.getStudy();

        if (user.isStudyEnrollmentResultByEmail()) {
            sendEmail(enrollmentEvent, user, event, study);
        }
        if (user.isStudyEnrollmentResultByWeb()) {
            createNotification(enrollmentEvent, user, event, study);
        }
    }

    // TODO: 링크 수정
    private void sendEmail(EnrollmentEvent enrollmentEvent, User user, Event event, Study study) {
        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("link", "/study/" + study.getEncodedPath() + "/events/" + event.getId());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", enrollmentEvent.getMessage());
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .subject(event.getTitle() + " 모임 참가 신청 결과입니다.")
                .to(user.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }

    private void createNotification(EnrollmentEvent enrollmentEvent, User user, Event event, Study study) {
        Notification notification = Notification.builder()
                .title(study.getTitle())
                .link("/study/" + study.getEncodedPath())
                .checked(false)
                .message(enrollmentEvent.getMessage())
                .user(user)
                .notificationType(NotificationType.STUDY_CREATED)
                .build();

        notificationRepository.save(notification);
    }
}
