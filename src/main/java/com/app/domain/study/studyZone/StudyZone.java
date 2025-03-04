package com.app.domain.study.studyZone;

import com.app.domain.study.Study;
import com.app.domain.zone.Zone;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@EqualsAndHashCode(of = {"study", "zone"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyZone {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_zone_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id")
    private Zone zone;

    @Builder
    public StudyZone(Study study, Zone zone) {
        this.study = study;
        this.zone = zone;
    }

    public void disconnectStudy() {
        this.study = null;
    }
}
