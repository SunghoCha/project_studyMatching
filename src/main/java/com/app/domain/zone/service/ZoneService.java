package com.app.domain.zone.service;

import com.app.domain.userZone.dto.UserZoneUpdateRequest;
import com.app.domain.zone.Zone;
import com.app.domain.zone.dto.ZoneResponse;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.global.error.ErrorCode;
import com.app.global.error.exception.InvalidZoneException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;


    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zones_kr.csv");
            List<Zone> zoneList = Files.readAllLines(resource.getFile().toPath(), UTF_8).stream()
                    .map(line -> {
                        String[] parts = line.split(",");
                        return Zone.builder()
                                .city(parts[0])
                                .localName(parts[1])
                                .province(parts[2])
                                .build();
                    })
                    .toList();
            zoneRepository.saveAll(zoneList);
        }
    }

    public Set<ZoneResponse> getAllZones() {
        return zoneRepository.findAll().stream()
                .map(ZoneResponse::of)
                .collect(Collectors.toSet());
    }

    public void validate(Set<UserZoneUpdateRequest> zoneRequests) {
        Set<Long> idList = zoneRequests.stream()
                .map(UserZoneUpdateRequest::getId)
                .collect(Collectors.toSet());

        List<Zone> findZones = zoneRepository.findAllById(idList);
        Map<Long, Zone> zoneMap = findZones.stream()
                .collect(Collectors.toMap(Zone::getId, Function.identity()));

        for (UserZoneUpdateRequest userZone : zoneRequests) {

            Zone matchingZone = zoneMap.get(userZone.getId());
            if (matchingZone == null
                    || !matchingZone.getCity().equals(userZone.getCity())
                    || !matchingZone.getLocalName().equals(userZone.getLocalName())
                    || !matchingZone.getProvince().equals(userZone.getProvince()))

                throw new InvalidZoneException();
        }
    }
}
