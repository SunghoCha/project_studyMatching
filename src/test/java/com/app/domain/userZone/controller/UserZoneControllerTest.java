package com.app.domain.userZone.controller;

import com.app.domain.user.User;
import com.app.domain.user.repository.UserRepository;
import com.app.domain.user.userZone.UserZone;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.web.servlet.MockMvc;
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
//    @Test
//    @WithAccount
//    @DisplayName("")
//    void edit_user_with_correct_input()
//            throws Exception {
//        // given
//        User user = getUserFromContext();
//
//        List<Zone> newZones = createZones("Seoul", "서울특별시", "none", 3);
//        List<Zone> findNewZones = zoneRepository.saveAll(newZones);
//
//        List<UserZoneUpdateRequest> requests = findNewZones.stream()
//                .map(zone -> UserZoneUpdateRequest.builder()
//                        .id(zone.getId())
//                        .city(zone.getCity())
//                        .localName(zone.getLocalName())
//                        .province(zone.getProvince())
//                        .build())
//                .collect(Collectors.toList());
//
//        String json = objectMapper.writeValueAsString(requests);
//
//        // when
//        mockMvc.perform(MockMvcRequestBuilders.patch("/user-zone/{userId}", user.getId())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json)
//                )
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(result -> {
//                    String content = result.getResponse().getContentAsString(UTF_8);
//
//                    List<UserZoneUpdateResponse> responses = objectMapper.readValue(
//                            content, new TypeReference<>() {}
//                    );
//
//                    assertThat(responses).containsExactlyInAnyOrder(
//                            new UserZoneUpdateResponse("Seoul0", "서울특별시0", "none0"),
//                            new UserZoneUpdateResponse("Seoul1", "서울특별시1", "none1"),
//                            new UserZoneUpdateResponse("Seoul2", "서울특별시2", "none2")
//                    );
//                })
//                .andDo(MockMvcResultHandlers.print());
//    }

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