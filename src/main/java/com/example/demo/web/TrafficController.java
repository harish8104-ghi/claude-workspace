package com.example.demo.web;

import com.example.demo.traffic.TrafficMetricsService;
import com.example.demo.web.dto.TrafficSnapshot;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/traffic")
@Tag(name = "API Traffic", description = "Request traffic dashboard data")
public class TrafficController {

    private final TrafficMetricsService trafficMetricsService;

    public TrafficController(TrafficMetricsService trafficMetricsService) {
        this.trafficMetricsService = trafficMetricsService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get API traffic statistics")
    public ResponseEntity<TrafficSnapshot> getTrafficStats() {
        return ResponseEntity.ok(trafficMetricsService.snapshot(Instant.now()));
    }
}
