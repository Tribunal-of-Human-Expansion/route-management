package com.groupb.routemanagement.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoutePolicyEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPolicyUpdate(String mapVersion, List<String> affectedSegments) {
        Map<String, Object> event = new HashMap<>();
        event.put("event_type", "ROUTE_POLICY_UPDATED");
        event.put("map_version", mapVersion);
        event.put("affected_segments", affectedSegments);
        event.put("timestamp", Instant.now().toString());

        kafkaTemplate.send("route-policy-updated", event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Published route-policy-updated event for version {}", mapVersion);
                    } else {
                        log.error("Failed to publish route-policy-updated event", ex);
                    }
                });
    }
}
