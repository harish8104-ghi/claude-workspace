package com.example.demo.traffic;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;

@Component
public class TrafficTrackingFilter extends OncePerRequestFilter {

    private final TrafficMetricsService trafficMetricsService;

    public TrafficTrackingFilter(TrafficMetricsService trafficMetricsService) {
        this.trafficMetricsService = trafficMetricsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !path.startsWith("/api/")
                || path.equals("/api/traffic")
                || path.startsWith("/api/traffic/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        long startedAt = System.nanoTime();
        int status = HttpServletResponse.SC_OK;
        try {
            filterChain.doFilter(request, response);
            status = response.getStatus();
        } catch (IOException | ServletException | RuntimeException exception) {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            throw exception;
        } finally {
            trafficMetricsService.record(
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    System.nanoTime() - startedAt,
                    Instant.now());
        }
    }
}
