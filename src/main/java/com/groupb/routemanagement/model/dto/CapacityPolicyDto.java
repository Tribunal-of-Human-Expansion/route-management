package com.groupb.routemanagement.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CapacityPolicyDto {

    @JsonProperty("vehicle_type")
    private String vehicleType;

    @JsonProperty("max_vehicles")
    private Integer maxVehicles;

    @JsonProperty("time_window_start")
    private LocalTime timeWindowStart;

    @JsonProperty("time_window_end")
    private LocalTime timeWindowEnd;

    @JsonProperty("valid_from")
    private LocalDate validFrom;

    @JsonProperty("valid_to")
    private LocalDate validTo;

}
