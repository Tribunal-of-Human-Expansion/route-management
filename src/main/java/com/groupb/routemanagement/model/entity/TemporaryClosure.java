package com.groupb.routemanagement.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "temporary_closures")
public class TemporaryClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "segment_id", length = 64, nullable = false)
    private String segmentId;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;

    @Column(name = "closure_start", nullable = false)
    private LocalDateTime closureStart;

    @Column(name = "closure_end")
    private LocalDateTime closureEnd;

    @Column(name = "map_version", length = 32, nullable = false)
    private String mapVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
