package com.example.demo.web;

import com.example.demo.SpringJava25DemoApplication;
import com.example.demo.traffic.TrafficRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = SpringJava25DemoApplication.class,
        properties = {
                "spring.datasource.url=jdbc:sqlite:./target/traffic-controller-test.db",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
class TrafficControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrafficRequestRepository trafficRequestRepository;

    @BeforeEach
    void clearTrafficRequests() {
        trafficRequestRepository.deleteAll();
    }

    @Test
    void stats_ReportsApiRequestsButDoesNotCountItsOwnPolling() throws Exception {
        mockMvc.perform(get("/api/greet"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/missing"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/traffic/stats").with(user("test-admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests").value(2))
                .andExpect(jsonPath("$.successfulRequests").value(1))
                .andExpect(jsonPath("$.clientErrors").value(1))
                .andExpect(jsonPath("$.requestsLastMinute").value(2))
                .andExpect(jsonPath("$.trafficByMinute.length()").value(60))
                .andExpect(jsonPath("$.recentRequests.length()").value(2));
    }
}
