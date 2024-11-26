package com.app.domain.zone.controller;

import com.app.TestUtils;
import com.app.domain.zone.Zone;
import com.app.domain.zone.repository.ZoneRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ZoneControllerTest {

    @Autowired
    ZoneRepository zoneRepository;
    @Autowired
    private MockMvc mockMvc;

//    @Test
//    @DisplayName("전체 존 조회 성공 테스트")
//    void get_zones_with_correct_input() throws Exception {
//        // given
//        List<Zone> zones = TestUtils.createZones(3);
//        zoneRepository.saveAll(zones);
//
//        // expected
//        mockMvc.perform(MockMvcRequestBuilders.get("/zone/allZones"))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$[0)
//                .andDo(MockMvcResultHandlers.print());
//    }
}