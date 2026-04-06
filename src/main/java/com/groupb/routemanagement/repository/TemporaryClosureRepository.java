package com.groupb.routemanagement.repository;

import com.groupb.routemanagement.model.entity.TemporaryClosure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface TemporaryClosureRepository extends JpaRepository<TemporaryClosure, Long> {
    List<TemporaryClosure> findBySegmentId(String segmentId);

    @Query("""
            select tc.segmentId
            from TemporaryClosure tc
            where tc.closureStart <= :now
              and (tc.closureEnd is null or tc.closureEnd >= :now)
            """)
    Set<String> findClosedSegmentIdsAt(@Param("now") LocalDateTime now);

    @Query("""
            select tc
            from TemporaryClosure tc
            where tc.segmentId = :segmentId
              and tc.closureStart <= :now
              and (tc.closureEnd is null or tc.closureEnd >= :now)
            order by tc.closureStart asc
            """)
    List<TemporaryClosure> findActiveBySegmentId(@Param("segmentId") String segmentId,
                                                 @Param("now") LocalDateTime now);
}
