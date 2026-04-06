package com.groupb.routemanagement.controller;

import com.groupb.routemanagement.model.dto.CapacityPolicyDto;
import com.groupb.routemanagement.model.dto.ClosureRequest;
import com.groupb.routemanagement.model.dto.RouteDecompositionRequest;
import com.groupb.routemanagement.model.dto.RouteDecompositionResponse;
import com.groupb.routemanagement.model.entity.MapVersion;
import com.groupb.routemanagement.model.entity.Segment;
import com.groupb.routemanagement.model.entity.SegmentCapacityPolicy;
import com.groupb.routemanagement.model.entity.TemporaryClosure;
import com.groupb.routemanagement.repository.MapVersionRepository;
import com.groupb.routemanagement.repository.SegmentRepository;
import com.groupb.routemanagement.repository.TemporaryClosureRepository;
import com.groupb.routemanagement.service.CapacityPolicyService;
import com.groupb.routemanagement.service.MapVersionService;
import com.groupb.routemanagement.service.RouteDecompositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteManagementController {

    private final RouteDecompositionService decompositionService;
    private final CapacityPolicyService capacityPolicyService;
    private final MapVersionService mapVersionService;
    private final SegmentRepository segmentRepository;
    private final MapVersionRepository mapVersionRepository;
    private final TemporaryClosureRepository temporaryClosureRepository;

    @PostMapping("/decompose")
    public ResponseEntity<RouteDecompositionResponse> decompose(@RequestBody RouteDecompositionRequest request) {
        RouteDecompositionResponse response = decompositionService.decomposeRoute(
                request.getOrigin(),
                request.getDestination(),
                request.getMapVersion()
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/segments")
    public ResponseEntity<List<Segment>> listSegments() {
        return ResponseEntity.ok(segmentRepository.findAll());
    }

    @PostMapping("/segments")
    public ResponseEntity<Segment> createSegment(@RequestBody Segment segment) {
        return ResponseEntity.ok(segmentRepository.save(segment));
    }

    @GetMapping("/segments/{segmentId}")
    public ResponseEntity<Segment> getSegment(@PathVariable String segmentId) {
        return segmentRepository.findById(segmentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/segments/{segmentId}/capacity")
    public ResponseEntity<SegmentCapacityPolicy> updateCapacity(
            @PathVariable String segmentId,
            @RequestBody CapacityPolicyDto dto) {
        SegmentCapacityPolicy policy = capacityPolicyService.updateCapacityPolicy(segmentId, dto);
        return ResponseEntity.ok(policy);
    }

    @PostMapping("/closures")
    public ResponseEntity<TemporaryClosure> applyClosure(
            @RequestParam String segmentId,
            @RequestBody ClosureRequest request) {
        TemporaryClosure closure = capacityPolicyService.applyClosure(segmentId, request);
        return ResponseEntity.ok(closure);
    }

    @DeleteMapping("/closures/{closureId}")
    public ResponseEntity<Void> liftClosure(@PathVariable Long closureId) {
        capacityPolicyService.liftClosure(closureId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/segments/{segmentId}/closures")
    public ResponseEntity<List<TemporaryClosure>> listActiveClosuresForSegment(@PathVariable String segmentId) {
        if (!segmentRepository.existsById(segmentId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(temporaryClosureRepository.findActiveBySegmentId(segmentId, LocalDateTime.now()));
    }

    @GetMapping("/map-version/current")
    public ResponseEntity<MapVersion> getCurrentMapVersion() {
        return mapVersionRepository.findByIsCurrentTrue()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/map-version/{version}")
    public ResponseEntity<MapVersion> getMapVersion(@PathVariable String version) {
        return mapVersionRepository.findById(version)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }
}
