package com.app.domain;

import com.app.domain.zone.Zone;

import java.util.ArrayList;
import java.util.List;

public class AbstractSettingTest {

    public List<Zone> createZones(int size) {
        List<Zone> zones = new ArrayList<>();
        for (int i = 1; i <= size; i++ ) {
            Zone zone = createZone("city" + i, "local" + i, "province" + i);
            zones.add(zone);
        }
        return zones;
    }

    private Zone createZone(String city, String localName, String province) {
        return Zone.builder()
                .city(city)
                .localName(localName)
                .province(province)
                .build();
    }
}
