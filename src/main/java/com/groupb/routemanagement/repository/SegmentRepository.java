package com.groupb.routemanagement.repository;

import com.groupb.routemanagement.model.entity.Segment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SegmentRepository extends JpaRepository<Segment, String> {
}
