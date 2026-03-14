package com.excel.controller;

import com.excel.entity.Task;
import com.excel.service.ClientService;
import com.excel.service.ColumnDefinitionService;
import com.excel.service.TaskDefinitionService;
import com.excel.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private TaskDefinitionService taskDefinitionService;

    @Mock
    private ClientService clientService;

    @Mock
    private ColumnDefinitionService columnDefinitionService;

    @InjectMocks
    private DashboardController dashboardController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    @Test
    void testGetOverview() throws Exception {
        Task completed = new Task();
        completed.setId(1L);
        completed.setStatus("已完成");

        Task running = new Task();
        running.setId(2L);
        running.setStatus("处理中");

        Task failed = new Task();
        failed.setId(3L);
        failed.setStatus("失败");

        when(taskService.list()).thenReturn(List.of(completed, running, failed));
        when(taskService.getRecentTasks(anyInt())).thenReturn(List.of(completed, running));
        when(taskDefinitionService.count()).thenReturn(5L);
        when(clientService.count()).thenReturn(3L);
        when(columnDefinitionService.count()).thenReturn(12L);

        mockMvc.perform(get("/api/dashboard/overview").param("recentLimit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskCount").value(3))
                .andExpect(jsonPath("$.completedCount").value(1))
                .andExpect(jsonPath("$.runningCount").value(1))
                .andExpect(jsonPath("$.failedCount").value(1))
                .andExpect(jsonPath("$.completionRate").value(33))
                .andExpect(jsonPath("$.recentTasks.length()").value(2));

        verify(taskService, times(1)).list();
        verify(taskService, times(1)).getRecentTasks(2);
    }
}
