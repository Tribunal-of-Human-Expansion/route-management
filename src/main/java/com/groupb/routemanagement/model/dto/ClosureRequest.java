package com.groupb.routemanagement.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClosureRequest {

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("closure_start")
    private LocalDateTime closureStart;

    @JsonProperty("closure_end")
    private LocalDateTime closureEnd;

}
