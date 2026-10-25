package com.example.demo.traffic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrafficRequestRepository extends JpaRepository<TrafficRequest, Long> {

    long countByStatusBetween(int firstStatus, int lastStatus);

    long countByRequestedAtEpochMillisBetween(long startEpochMillis, long endEpochMillis);

    @Query("select request from TrafficRequest request order by request.requestedAtEpochMillis desc")
    List<TrafficRequest> findRecentRequests(Pageable pageable);

    @Query("select avg(request.durationMs) from TrafficRequest request")
    Double findAverageDurationMs();

    @Query("""
            select request.requestedAtEpochMillis
            from TrafficRequest request
            where request.requestedAtEpochMillis between :startEpochMillis and :endEpochMillis
            """)
    List<Long> findRequestTimestamps(
            @Param("startEpochMillis") long startEpochMillis,
            @Param("endEpochMillis") long endEpochMillis);
}
