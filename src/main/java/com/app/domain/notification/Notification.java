package com.app.domain.notification;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String title;

    private String link;

    private String message;

    private boolean checked;

    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Builder
    public Notification(String title, String link, String message, boolean checked, User user, NotificationType notificationType) {
        this.title = title;
        this.link = link;
        this.message = message;
        this.checked = checked;
        this.user = user;
        this.notificationType = notificationType;
    }
}
