package com.groupb.routemanagement.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteDecompositionResponse {

    @JsonProperty("map_version")
    private String mapVersion;

    @JsonProperty("segments")
    private List<SegmentDto> segments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SegmentDto {
        @JsonProperty("segment_id")
        private String segmentId;

        @JsonProperty("authoritative_region")
        private String authoritativeRegion;

        @JsonProperty("time_window_start")
        private Instant timeWindowStart;

        @JsonProperty("time_window_end")
        private Instant timeWindowEnd;
    }
}
