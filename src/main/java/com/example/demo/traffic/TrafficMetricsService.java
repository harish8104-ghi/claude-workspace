package com.example.demo.traffic;

import com.example.demo.web.dto.TrafficSnapshot;
import com.example.demo.web.dto.TrafficSnapshot.RecentRequest;
import com.example.demo.web.dto.TrafficSnapshot.TrafficPoint;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrafficMetricsService {

    private static final int MINUTE_BUCKET_COUNT = 60;
    private static final long MILLIS_PER_MINUTE = 60_000;

    private final TrafficRequestRepository repository;

    public TrafficMetricsService(TrafficRequestRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void record(String method, String path, int status, long durationNanos, Instant recordedAt) {
        repository.save(new TrafficRequest(
                recordedAt.toEpochMilli(),
                method,
                path,
                status,
                durationNanos / 1_000_000.0));
    }

    @Transactional(readOnly = true)
    public TrafficSnapshot snapshot(Instant now) {
        long nowMillis = now.toEpochMilli();
        long hourAgoMillis = nowMillis - MINUTE_BUCKET_COUNT * MILLIS_PER_MINUTE;
        long minuteStartMillis = nowMillis - MILLIS_PER_MINUTE;
        long totalRequests = repository.count();

        Map<Long, Long> requestsPerMinute = new HashMap<>();
        for (long timestamp : repository.findRequestTimestamps(hourAgoMillis, nowMillis)) {
            long minute = Math.floorDiv(timestamp, MILLIS_PER_MINUTE);
            requestsPerMinute.merge(minute, 1L, Long::sum);
        }

        long currentMinute = Math.floorDiv(nowMillis, MILLIS_PER_MINUTE);
        List<TrafficPoint> trafficByMinute = new ArrayList<>(MINUTE_BUCKET_COUNT);
        for (long minute = currentMinute - MINUTE_BUCKET_COUNT + 1; minute <= currentMinute; minute++) {
            trafficByMinute.add(new TrafficPoint(
                    Instant.ofEpochMilli(minute * MILLIS_PER_MINUTE).toString(),
                    requestsPerMinute.getOrDefault(minute, 0L)));
        }

        List<RecentRequest> recentRequests = repository.findRecentRequests(PageRequest.of(0, 100))
                .stream()
                .map(request -> new RecentRequest(
                        Instant.ofEpochMilli(request.getRequestedAtEpochMillis()).toString(),
                        request.getMethod(),
                        request.getPath(),
                        request.getStatus(),
                        request.getDurationMs()))
                .toList();

        return new TrafficSnapshot(
                totalRequests,
                repository.countByStatusBetween(200, 299),
                repository.countByStatusBetween(300, 399),
                repository.countByStatusBetween(400, 499),
                repository.countByStatusBetween(500, 599),
                nullToZero(repository.findAverageDurationMs()),
                repository.countByRequestedAtEpochMillisBetween(minuteStartMillis, nowMillis),
                trafficByMinute,
                recentRequests);
    }

    private static double nullToZero(Double value) {
        return value == null ? 0 : value;
    }
}
