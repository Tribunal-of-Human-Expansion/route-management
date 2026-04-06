package com.groupb.routemanagement.service;

import com.groupb.routemanagement.model.entity.MapVersion;
import com.groupb.routemanagement.repository.MapVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MapVersionService {

    private final MapVersionRepository mapVersionRepository;

    public String getCurrentMapVersion() {
        return mapVersionRepository.findByIsCurrentTrue()
                .map(MapVersion::getVersion)
                .orElse("v2025.04"); // Default fallback
    }

    @Transactional
    public String bumpMapVersion(String description) {
        mapVersionRepository.findByIsCurrentTrue().ifPresent(v -> {
            v.setCurrent(false);
            mapVersionRepository.save(v);
        });

        String newVersion = "v" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HHmmss"));
        
        MapVersion newMapVersion = new MapVersion();
        newMapVersion.setVersion(newVersion);
        newMapVersion.setDescription(description);
        newMapVersion.setCurrent(true);
        
        mapVersionRepository.save(newMapVersion);
        return newVersion;
    }
}
