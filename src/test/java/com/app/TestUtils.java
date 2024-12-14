package com.app;

import com.app.domain.tag.Tag;
import com.app.domain.zone.Zone;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.ArrayList;
import java.util.List;

public class TestUtils {

    public static String getAuthenticatedEmail() {
        OAuth2AuthenticationToken authentication =
                (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        return (String) authentication.getPrincipal()
                .getAttribute("email");
    }

    public static List<Zone> createZones(int size) {
        List<Zone> zones = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Zone zone = createZone("city" + i, "local" + i, "province" + i);
            zones.add(zone);
        }
        return zones;
    }

    public static Zone createZone(String city, String localName, String province) {
        return Zone.builder()
                .city(city)
                .localName(localName)
                .province(province)
                .build();
    }

    public static List<Tag> createTags(int size) {
        List<Tag> tags = new ArrayList<>();
        for (int i = 1; i <= size; i++) {
            Tag tag = Tag.builder()
                    .title("tag" + i)
                    .build();
            tags.add(tag);
        }
        return tags;
    }

    public static List<Tag> createTagsByRange(int from, int to) {
        List<Tag> tags = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            Tag tag = Tag.builder()
                    .title("tag" + i)
                    .build();
            tags.add(tag);
        }
        return tags;
    }
}
