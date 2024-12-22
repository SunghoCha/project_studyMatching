package com.app.domain.userZone.controller;

import com.app.TestUtils;
import com.app.WithAccount;
import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.user.userZone.UserZone;
import com.app.domain.user.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.user.userZone.dto.UserZoneUpdateResponse;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.global.error.exception.UserNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserZoneControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ZoneRepository zoneRepository;


    // TODO 추후 수정
    @Test
    @WithAccount
    @DisplayName("")
    void edit_user_with_correct_input()
            throws Exception {
        // given
        User user = userRepository.findByEmail(TestUtils.getAuthenticatedEmail()).orElseThrow(UserNotFoundException::new);

        List<Zone> newZones = createZones("Seoul", "서울특별시", "none", 3);
        List<Zone> foundNewZones = zoneRepository.saveAll(newZones);

        Set<Long> zoneIds = foundNewZones.stream()
                .map(Zone::getId)
                .collect(Collectors.toSet());

        UserZoneUpdateRequest request = UserZoneUpdateRequest.builder()
                .zoneIds(zoneIds)
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders.patch("/user-zone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.zones[0].city").value("Seoul0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zones[0].localName").value("서울특별시0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zones[0].province").value("none0"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zones[2].city").value("Seoul2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zones[2].localName").value("서울특별시2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.zones[2].province").value("none2"))
                .andDo(MockMvcResultHandlers.print());
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