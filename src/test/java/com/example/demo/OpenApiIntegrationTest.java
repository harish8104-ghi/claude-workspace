package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

import com.example.demo.traffic.TrafficRequestRepository;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = SpringJava25DemoApplication.class,
        properties = {
                "spring.datasource.url=jdbc:sqlite:./target/openapi-test.db",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@AutoConfigureMockMvc
class OpenApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrafficRequestRepository trafficRequestRepository;

    @BeforeEach
    void clearTrafficRequests() {
        trafficRequestRepository.deleteAll();
    }

    @Test
    void openApiDocument_DescribesGreetingEndpoint() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Spring Java 25 Demo API"))
                .andExpect(jsonPath("$.paths['/api/greet'].get.summary").value("Get a greeting"))
                .andExpect(jsonPath("$.paths['/api/greet'].get.parameters[0].name").value("name"))
                .andExpect(jsonPath("$.paths['/api/greet'].get.responses['200']").exists());
    }

    @Test
    void swaggerUi_IsAvailable() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void trafficDashboard_IsAvailableAtRoot() throws Exception {
        mockMvc.perform(get("/").with(user("test-admin").roles("ADMIN")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/index.html").with(user("test-admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("API traffic")));
    }

    @Test
    void trafficStatsEndpoint_IsAvailable() throws Exception {
        mockMvc.perform(get("/api/traffic/stats").with(user("test-admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRequests").isNumber())
                .andExpect(jsonPath("$.trafficByMinute.length()").value(60))
                .andExpect(jsonPath("$.recentRequests").isArray());
    }
}
