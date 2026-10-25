package com.example.demo.web;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GreetingControllerTest {

    @Test
    void greet_DefaultName_ReturnsHelloWorld() throws Exception {
        GreetingController controller = new GreetingController();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/greet"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"Hello, World!\"}"));
    }
}
