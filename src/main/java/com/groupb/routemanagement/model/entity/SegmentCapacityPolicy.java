package com.groupb.routemanagement.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "segment_capacity_policies")
public class SegmentCapacityPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "segment_id", length = 64, nullable = false)
    private String segmentId;

    @Column(name = "map_version", length = 32, nullable = false)
    private String mapVersion;

    @Column(name = "vehicle_type", length = 32, nullable = false)
    private String vehicleType = "ANY";

    @Column(name = "max_vehicles", nullable = false)
    private Integer maxVehicles;

    @Column(name = "time_window_start")
    private LocalTime timeWindowStart;

    @Column(name = "time_window_end")
    private LocalTime timeWindowEnd;

    @Column(name = "valid_from", nullable = false)
    private LocalDate validFrom;

    @Column(name = "valid_to")
    private LocalDate validTo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
