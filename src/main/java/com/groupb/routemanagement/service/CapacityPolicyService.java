package com.groupb.routemanagement.service;

import com.groupb.routemanagement.event.RoutePolicyEventPublisher;
import com.groupb.routemanagement.model.dto.CapacityPolicyDto;
import com.groupb.routemanagement.model.dto.ClosureRequest;
import com.groupb.routemanagement.model.entity.SegmentCapacityPolicy;
import com.groupb.routemanagement.model.entity.TemporaryClosure;
import com.groupb.routemanagement.repository.CapacityPolicyRepository;
import com.groupb.routemanagement.repository.SegmentRepository;
import com.groupb.routemanagement.repository.TemporaryClosureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CapacityPolicyService {

    private final CapacityPolicyRepository capacityPolicyRepository;
    private final TemporaryClosureRepository temporaryClosureRepository;
    private final SegmentRepository segmentRepository;
    private final MapVersionService mapVersionService;
    private final RoutePolicyEventPublisher eventPublisher;

    @Transactional
    public SegmentCapacityPolicy updateCapacityPolicy(String segmentId, CapacityPolicyDto dto) {
        if (!segmentRepository.existsById(segmentId)) {
            throw new IllegalArgumentException("Segment not found: " + segmentId);
        }

        String newVersion = mapVersionService.bumpMapVersion("Capacity policy updated for " + segmentId);

        SegmentCapacityPolicy policy = new SegmentCapacityPolicy();
        policy.setSegmentId(segmentId);
        policy.setMapVersion(newVersion);
        policy.setVehicleType(dto.getVehicleType() != null ? dto.getVehicleType() : "ANY");
        policy.setMaxVehicles(dto.getMaxVehicles());
        policy.setTimeWindowStart(dto.getTimeWindowStart());
        policy.setTimeWindowEnd(dto.getTimeWindowEnd());
        policy.setValidFrom(dto.getValidFrom());
        policy.setValidTo(dto.getValidTo());

        policy = capacityPolicyRepository.save(policy);

        eventPublisher.publishPolicyUpdate(newVersion, List.of(segmentId));

        return policy;
    }

    @Transactional
    public TemporaryClosure applyClosure(String segmentId, ClosureRequest request) {
        if (!segmentRepository.existsById(segmentId)) {
            throw new IllegalArgumentException("Segment not found: " + segmentId);
        }

        String newVersion = mapVersionService.bumpMapVersion("Closure applied to " + segmentId);

        TemporaryClosure closure = new TemporaryClosure();
        closure.setSegmentId(segmentId);
        closure.setReason(request.getReason());
        closure.setClosureStart(request.getClosureStart());
        closure.setClosureEnd(request.getClosureEnd());
        closure.setMapVersion(newVersion);

        closure = temporaryClosureRepository.save(closure);

        eventPublisher.publishPolicyUpdate(newVersion, List.of(segmentId));

        return closure;
    }

    @Transactional
    public void liftClosure(Long closureId) {
        TemporaryClosure closure = temporaryClosureRepository.findById(closureId)
                .orElseThrow(() -> new IllegalArgumentException("Closure not found: " + closureId));

        String segmentId = closure.getSegmentId();
        temporaryClosureRepository.delete(closure);

        String newVersion = mapVersionService.bumpMapVersion("Closure lifted from " + segmentId);
        eventPublisher.publishPolicyUpdate(newVersion, List.of(segmentId));
    }
}
