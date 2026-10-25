package com.example.demo.web.dto;

import java.util.List;

public record TrafficSnapshot(
        long totalRequests,
        long successfulRequests,
        long redirectRequests,
        long clientErrors,
        long serverErrors,
        double averageResponseTimeMs,
        long requestsLastMinute,
        List<TrafficPoint> trafficByMinute,
        List<RecentRequest> recentRequests) {

    public record TrafficPoint(String timestamp, long requests) {
    }

    public record RecentRequest(
            String timestamp,
            String method,
            String path,
            int status,
            double durationMs) {
    }
}
