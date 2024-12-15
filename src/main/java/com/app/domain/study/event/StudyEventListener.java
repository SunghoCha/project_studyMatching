package com.app.domain.study.event;

import com.app.domain.notification.Notification;
import com.app.domain.notification.NotificationType;
import com.app.domain.notification.repository.NotificationRepository;
import com.app.domain.study.Study;
import com.app.domain.study.repository.StudyRepository;
import com.app.domain.study.studyTag.StudyTag;
import com.app.domain.study.studyZone.StudyZone;
import com.app.domain.tag.Tag;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.zone.Zone;
import com.app.global.config.properties.AppProperties;
import com.app.global.error.exception.StudyNotFoundException;
import com.app.infra.email.EmailMessage;
import com.app.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class StudyEventListener {

    private final StudyRepository studyRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleStudyCreatedEvent(StudyCreatedEvent studyCreatedEvent) {
        Study study = studyRepository.findStudyWithTagsAndZonesById(studyCreatedEvent.getStudy().getId())
                .orElseThrow(StudyNotFoundException::new);

        List<Tag> tags = study.getStudyTags().stream().map(StudyTag::getTag).toList();
        List<Zone> zones = study.getStudyZones().stream().map(StudyZone::getZone).toList();
        List<User> users = userRepository.findUserByTagsAndZones(tags, zones);

        users.forEach(user -> {
            if (user.isStudyCreatedByEmail()) {
                sendStudyCreatedByEmail(user, study);
            }
            if (user.isStudyCreatedByWeb()) {
                saveStudyCreatedNotification(user, study);
            }
        });

        log.info("{} is created.", study.getTitle());
        // TODO: 이메일 보내거나 DB에 notification 정보 전달
    }

    private void saveStudyCreatedNotification(User user, Study study) {
        Notification notification = Notification.builder()
                .title(study.getTitle())
                .link("/study/" + study.getEncodedPath())
                .checked(false)
                .message(study.getShortDescription())
                .user(user)
                .notificationType(NotificationType.STUDY_CREATED)
                .build();
        notificationRepository.save(notification);
    }

    private void sendStudyCreatedByEmail(User user, Study study) {
        Context context = new Context();
        context.setVariable("name", user.getName());
        context.setVariable("link", "/study/" + study.getEncodedPath());
        context.setVariable("linkName", study.getTitle());
        context.setVariable("message", "새로운 스터디가 생성되었습니다.");
        context.setVariable("host", appProperties.getHost());

        String message = templateEngine.process("mail/simple-link", context);
        EmailMessage emailMessage = EmailMessage.builder()
                .subject("관심 스터디, '" + study.getTitle() + "' 가 생성되었습니다.")
                .to(user.getEmail())
                .message(message)
                .build();

        emailService.sendEmail(emailMessage);
    }
}
