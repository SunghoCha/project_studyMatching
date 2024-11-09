package com.app.domain.zone;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@EqualsAndHashCode(of = {"city", "localName", "province"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Zone {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String city;

    @Column(nullable = false)
    private String localName;

    @Column(nullable = true)
    private String province;

    @Builder
    private Zone(String city, String localName, String province) {
        this.city = city;
        this.localName = localName;
        this.province = province;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)/%s", city, localName, province);
    }
}
