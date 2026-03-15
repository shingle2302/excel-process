package com.excel.controller;

import com.excel.service.DataSourceConfigService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DataSourceConfigControllerTest {

    @Mock
    private DataSourceConfigService dataSourceConfigService;

    @InjectMocks
    private DataSourceConfigController dataSourceConfigController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dataSourceConfigController).build();
    }

    @Test
    void testTestConnection() throws Exception {
        doNothing().when(dataSourceConfigService).validateConnection(any());

        mockMvc.perform(post("/api/data-sources/test-connection")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  \"name\": \"测试数据源\",
                                  \"type\": \"mysql\",
                                  \"connectionConfig\": \"{\\\"host\\\":\\\"127.0.0.1\\\",\\\"port\\\":3306}\"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
