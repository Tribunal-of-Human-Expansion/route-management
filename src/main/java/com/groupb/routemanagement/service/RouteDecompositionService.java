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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
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

        String mapVersion = requestedMapVersion != null ? requestedMapVersion : mapVersionService.getCurrentMapVersion();

        List<Segment> allSegments = segmentRepository.findAll();
        Set<String> closedSegmentIds = temporaryClosureRepository.findClosedSegmentIdsAt(LocalDateTime.now());

        List<Segment> traversableSegments = allSegments.stream()
                .filter(Segment::isActive)
                .filter(segment -> !closedSegmentIds.contains(segment.getSegmentId()))
                .toList();

        List<Segment> routeSegments = findPathSegments(origin, destination, traversableSegments);

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
        if (origin.equals(destination)) {
            return Collections.emptyList();
        }

        Map<String, List<Segment>> adjacency = new HashMap<>();
        for (Segment segment : segments) {
            adjacency.computeIfAbsent(segment.getOriginNode(), key -> new ArrayList<>()).add(segment);
        }

        Queue<String> queue = new LinkedList<>();
        queue.add(origin);

        Set<String> visitedNodes = new HashSet<>();
        visitedNodes.add(origin);

        Map<String, Segment> previousSegmentByNode = new HashMap<>();

        while (!queue.isEmpty()) {
            String currentNode = queue.poll();
            List<Segment> outgoing = adjacency.getOrDefault(currentNode, Collections.emptyList());

            for (Segment segment : outgoing) {
                String nextNode = segment.getDestinationNode();
                if (visitedNodes.contains(nextNode)) {
                    continue;
                }

                visitedNodes.add(nextNode);
                previousSegmentByNode.put(nextNode, segment);

                if (Objects.equals(nextNode, destination)) {
                    return buildPath(destination, previousSegmentByNode);
                }

                queue.add(nextNode);
            }
        }

        throw new IllegalArgumentException(
                "No route found from " + origin + " to " + destination + " with current segment availability");
    }

    private List<Segment> buildPath(String destination, Map<String, Segment> previousSegmentByNode) {
        List<Segment> reversedPath = new ArrayList<>();
        String cursor = destination;

        while (previousSegmentByNode.containsKey(cursor)) {
            Segment segment = previousSegmentByNode.get(cursor);
            reversedPath.add(segment);
            cursor = segment.getOriginNode();
        }

        Collections.reverse(reversedPath);
        return reversedPath;
    }
}
