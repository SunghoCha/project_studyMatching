package com.app;

import com.app.domain.zone.Zone;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static List<Zone> createZones(int size) {
        List<Zone> zones = new ArrayList<>();
        for (int i = 1; i <= size; i++ ) {
            Zone zone = createZone("city" + i, "local" + i, "province" + i);
            zones.add(zone);
        }
        return zones;
    }

    private static Zone createZone(String city, String localName, String province) {
        return Zone.builder()
                .city(city)
                .localName(localName)
                .province(province)
                .build();
    }


}
