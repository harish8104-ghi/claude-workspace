package com.example.demo.traffic;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(
        name = "api_traffic_requests",
        indexes = @Index(name = "idx_api_traffic_requested_at", columnList = "requested_at_epoch_ms"))
public class TrafficRequest {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String id;

    @Column(name = "requested_at_epoch_ms", nullable = false)
    private long requestedAtEpochMillis;

    @Column(nullable = false, length = 12)
    private String method;

    @Column(nullable = false, length = 2048)
    private String path;

    @Column(nullable = false)
    private int status;

    @Column(name = "duration_ms", nullable = false)
    private double durationMs;

    protected TrafficRequest() {
    }

    public TrafficRequest(long requestedAtEpochMillis, String method, String path, int status, double durationMs) {
        this.id = UUID.randomUUID().toString();
        this.requestedAtEpochMillis = requestedAtEpochMillis;
        this.method = method;
        this.path = path;
        this.status = status;
        this.durationMs = durationMs;
    }

    public long getRequestedAtEpochMillis() {
        return requestedAtEpochMillis;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getStatus() {
        return status;
    }

    public double getDurationMs() {
        return durationMs;
    }
}
