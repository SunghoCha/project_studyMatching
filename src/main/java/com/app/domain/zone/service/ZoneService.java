package com.app.domain.zone.service;

import com.app.domain.zone.Zone;
import com.app.domain.zone.dto.ZoneResponse;
import com.app.domain.zone.repository.ZoneRepository;
import com.app.global.error.exception.ZoneNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@Transactional
@RequiredArgsConstructor
public class ZoneService {

    private final ZoneRepository zoneRepository;

    public Zone findByCityAndLocalNameAndProvince(String city, String localName, String province) {
        return zoneRepository.findByCityAndLocalNameAndProvince(city, localName, province).orElseThrow(ZoneNotFoundException::new);
    }

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

//    public void validate(Set<UserZoneUpdateRequest> zoneRequests) {
//        Set<Long> idList = zoneRequests.stream()
//                .map(UserZoneUpdateRequest::getId)
//                .collect(Collectors.toSet());
//
//        List<Zone> findZones = zoneRepository.findAllById(idList);
//        Map<Long, Zone> zoneMap = findZones.stream()
//                .collect(Collectors.toMap(Zone::getId, Function.identity()));
//
//        for (UserZoneUpdateRequest userZone : zoneRequests) {
//
//            Zone matchingZone = zoneMap.get(userZone.getId());
//            if (matchingZone == null
//                    || !matchingZone.getCity().equals(userZone.getCity())
//                    || !matchingZone.getLocalName().equals(userZone.getLocalName())
//                    || !matchingZone.getProvince().equals(userZone.getProvince()))
//
//                throw new InvalidZoneException();
//        }
//    }

    public Zone findById(Long zoneId) {
        return zoneRepository.findById(zoneId).orElseThrow(ZoneNotFoundException::new);
    }

    public List<Zone> findAll() {
        return zoneRepository.findAll();
    }

    public List<Zone> findByIdIn(Set<Long> zoneIds) {
        List<Zone> zones = zoneRepository.findByIdIn(zoneIds);

        if (zoneIds.size() != zones.size()) {
            throw new ZoneNotFoundException();
        }
        return zones;
    }
}
