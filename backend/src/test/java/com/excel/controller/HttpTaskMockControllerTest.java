package com.excel.controller;

import com.excel.controller.mock.HttpTaskMockController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HttpTaskMockControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new HttpTaskMockController()).build();
    }

    @Test
    void testAuthLoginAndSecureQuery() throws Exception {
        mockMvc.perform(post("/api/mock-http-source/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "demo",
                                  "password": "demo123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-token-123"));

        mockMvc.perform(post("/api/mock-http-source/query/secure")
                        .header("Authorization", "Bearer mock-token-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bizDate\":\"2026-03-15\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authRequired").value(true));
    }

    @Test
    void testPublicQuery() throws Exception {
        mockMvc.perform(post("/api/mock-http-source/query/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"active\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authRequired").value(false));
    }
}
