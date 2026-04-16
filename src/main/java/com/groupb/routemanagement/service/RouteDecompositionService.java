package com.groupb.routemanagement.service;

import com.groupb.routemanagement.model.dto.RouteDecompositionResponse;
import com.groupb.routemanagement.model.entity.Segment;
import com.groupb.routemanagement.repository.SegmentRepository;
import com.groupb.routemanagement.repository.TemporaryClosureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RouteDecompositionService {

    private final SegmentRepository segmentRepository;
    private final TemporaryClosureRepository temporaryClosureRepository;
    private final MapVersionService mapVersionService;

    public RouteDecompositionResponse decomposeRoute(String origin, String destination, String requestedMapVersion) {
        if (origin == null || origin.isBlank() || destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Origin and destination are required");
        }

        // Ensure service handles uppercase entries
        final String upperOrigin = origin.toUpperCase();
        final String upperDest = destination.toUpperCase();

        String mapVersion = requestedMapVersion != null ? requestedMapVersion : mapVersionService.getCurrentMapVersion();

        List<Segment> allSegments = segmentRepository.findAll();
        Set<String> closedSegmentIds = temporaryClosureRepository.findClosedSegmentIdsAt(LocalDateTime.now());

        List<Segment> traversableSegments = allSegments.stream()
                .filter(Segment::isActive)
                .filter(segment -> !closedSegmentIds.contains(segment.getSegmentId()))
                .toList();

        List<Segment> routeSegments = findPathSegments(upperOrigin, upperDest, traversableSegments);

        List<RouteDecompositionResponse.SegmentDto> segmentDtos = new ArrayList<>();
        Instant timeCursor = Instant.now();
        
        for (Segment seg : routeSegments) {
            RouteDecompositionResponse.SegmentDto dto = new RouteDecompositionResponse.SegmentDto();
            dto.setSegmentId(seg.getSegmentId());
            dto.setAuthoritativeRegion(seg.getAuthoritativeRegion());
            dto.setTimeWindowStart(timeCursor);
            
            // Assume 1 hour per segment for demo
            timeCursor = timeCursor.plus(1, ChronoUnit.HOURS);
            dto.setTimeWindowEnd(timeCursor);
            segmentDtos.add(dto);
        }

        RouteDecompositionResponse response = new RouteDecompositionResponse();
        response.setMapVersion(mapVersion);
        response.setSegments(segmentDtos);

        return response;
    }

    private List<Segment> findPathSegments(String origin, String destination, List<Segment> segments) {
        if (origin.equalsIgnoreCase(destination)) {
            return Collections.emptyList();
        }

        return segments.stream()
                .filter(s -> 
                    (origin.equalsIgnoreCase(s.getOriginNode()) && destination.equalsIgnoreCase(s.getDestinationNode())) ||
                    (origin.equalsIgnoreCase(s.getDestinationNode()) && destination.equalsIgnoreCase(s.getOriginNode()))
                )
                .findFirst()
                .map(Collections::singletonList)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No direct route found from " + origin + " to " + destination + " (or vice-versa) with current segment availability"));
    }
}
