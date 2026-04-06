package com.groupb.routemanagement.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "segments")
public class Segment {

    @Id
    @Column(name = "segment_id", length = 64)
    private String segmentId;

    @Column(name = "origin_node", length = 128, nullable = false)
    private String originNode;

    @Column(name = "destination_node", length = 128, nullable = false)
    private String destinationNode;

    @Column(name = "authoritative_region", length = 64, nullable = false)
    private String authoritativeRegion;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

}
