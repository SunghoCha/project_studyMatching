package com.app.domain.userZone;

import com.app.domain.common.BaseTimeEntity;
import com.app.domain.user.User;
import com.app.domain.zone.Zone;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@EqualsAndHashCode(of = {"user", "zone"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserZone extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_zone_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @Builder
    public UserZone(User user, Zone zone) {
        this.user = user;
        this.zone = zone;
    }
}
