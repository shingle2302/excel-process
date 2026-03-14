package com.excel.controller;

import com.excel.entity.Client;
import com.excel.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testLogin() throws Exception {
        Client client = new Client();
        client.setClientId("admin");
        client.setClientName("系统管理员");

        when(clientService.validateClient(anyString(), anyString())).thenReturn(true);
        when(clientService.getByClientId("admin")).thenReturn(Optional.of(client));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\":\"admin\",\"clientSecret\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiKey").value("admin"))
                .andExpect(jsonPath("$.clientName").value("系统管理员"));

        verify(clientService, times(1)).validateClient("admin", "admin123");
    }
}
