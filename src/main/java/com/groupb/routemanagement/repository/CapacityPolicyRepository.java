package com.groupb.routemanagement.repository;

import com.groupb.routemanagement.model.entity.SegmentCapacityPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CapacityPolicyRepository extends JpaRepository<SegmentCapacityPolicy, Long> {
    List<SegmentCapacityPolicy> findBySegmentId(String segmentId);
}
