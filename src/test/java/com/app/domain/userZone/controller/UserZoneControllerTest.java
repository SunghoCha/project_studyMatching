package com.app.domain.userZone.controller;

import com.app.WithAccount;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.userZone.UserZone;
import com.app.domain.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.userZone.dto.UserZoneUpdateResponse;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class UserZoneControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ZoneRepository zoneRepository;


    @Test
    @WithAccount
    @DisplayName("")
    void edit_user_with_correct_input()
            throws Exception {
        // given
        User user = getUserFromContext();

        List<Zone> newZones = createZones("Seoul", "서울특별시", "none", 3);
        List<Zone> findNewZones = zoneRepository.saveAll(newZones);

        List<UserZoneUpdateRequest> requests = findNewZones.stream()
                .map(zone -> UserZoneUpdateRequest.builder()
                        .id(zone.getId())
                        .city(zone.getCity())
                        .localName(zone.getLocalName())
                        .province(zone.getProvince())
                        .build())
                .collect(Collectors.toList());

        String json = objectMapper.writeValueAsString(requests);

        // when
        mockMvc.perform(MockMvcRequestBuilders.patch("/user-zone/{userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    String content = result.getResponse().getContentAsString(UTF_8);

                    List<UserZoneUpdateResponse> responses = objectMapper.readValue(
                            content, new TypeReference<>() {}
                    );

                    assertThat(responses).containsExactlyInAnyOrder(
                            new UserZoneUpdateResponse("Seoul0", "서울특별시0", "none0"),
                            new UserZoneUpdateResponse("Seoul1", "서울특별시1", "none1"),
                            new UserZoneUpdateResponse("Seoul2", "서울특별시2", "none2")
                    );
                })
                .andDo(MockMvcResultHandlers.print());
    }

    private User getUserFromContext() {
        OAuth2User principal = (OAuth2User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = (String) principal.getAttributes().get("email");

        return userRepository.findByEmail(email).orElseThrow(IllegalArgumentException::new);
    }

    private List<UserZone> createUserZones(User user, Set<Zone> zones) {
        return zones.stream()
                .map(zone -> UserZone.builder()
                        .user(user)
                        .zone(zone)
                        .build())
                .collect(Collectors.toList());
    }

    private List<Zone> createZones(String city, String localName, String province, int size) {
        List<Zone> zones = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Zone zone = Zone.builder()
                    .city(city + i)
                    .localName(localName + i)
                    .province(province + i)
                    .build();
            zones.add(zone);
        }
        return zones;
    }

}