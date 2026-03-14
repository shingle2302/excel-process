package com.excel.controller;

import com.excel.entity.TaskDefinition;
import com.excel.service.TaskDefinitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TaskDefinitionControllerTest {

    @Mock
    private TaskDefinitionService taskDefinitionService;

    @InjectMocks
    private TaskDefinitionController taskDefinitionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskDefinitionController).build();
    }

    @Test
    void testCreateTaskDefinition() throws Exception {
        // 准备测试数据
        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setId(1L);
        taskDefinition.setName("test-task");

        // 模拟方法调用
        doNothing().when(taskDefinitionService).save(any(TaskDefinition.class));

        // 执行测试
        mockMvc.perform(post("/api/task-definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"test-task\", \"type\": \"导出\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test-task"));

        // 验证结果
        verify(taskDefinitionService, times(1)).save(any(TaskDefinition.class));
    }

    @Test
    void testGetTaskDefinitions() throws Exception {
        // 准备测试数据
        TaskDefinition taskDefinition1 = new TaskDefinition();
        taskDefinition1.setId(1L);
        taskDefinition1.setName("test-task1");

        TaskDefinition taskDefinition2 = new TaskDefinition();
        taskDefinition2.setId(2L);
        taskDefinition2.setName("test-task2");

        List<TaskDefinition> taskDefinitionList = new ArrayList<>();
        taskDefinitionList.add(taskDefinition1);
        taskDefinitionList.add(taskDefinition2);

        // 模拟方法调用
        when(taskDefinitionService.list()).thenReturn(taskDefinitionList);

        // 执行测试
        mockMvc.perform(get("/api/task-definitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        // 验证结果
        verify(taskDefinitionService, times(1)).list();
    }

    @Test
    void testGetTaskDefinition() throws Exception {
        // 准备测试数据
        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setId(1L);
        taskDefinition.setName("test-task");

        // 模拟方法调用
        when(taskDefinitionService.getById(anyLong())).thenReturn(taskDefinition);

        // 执行测试
        mockMvc.perform(get("/api/task-definitions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test-task"));

        // 验证结果
        verify(taskDefinitionService, times(1)).getById(anyLong());
    }

    @Test
    void testUpdateTaskDefinition() throws Exception {
        // 准备测试数据
        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setId(1L);
        taskDefinition.setName("updated-task");

        // 模拟方法调用
        doNothing().when(taskDefinitionService).updateById(any(TaskDefinition.class));

        // 执行测试
        mockMvc.perform(put("/api/task-definitions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"updated-task\", \"type\": \"导出\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updated-task"));

        // 验证结果
        verify(taskDefinitionService, times(1)).updateById(any(TaskDefinition.class));
    }

    @Test
    void testDeleteTaskDefinition() throws Exception {
        // 模拟方法调用
        doNothing().when(taskDefinitionService).removeById(anyLong());

        // 执行测试
        mockMvc.perform(delete("/api/task-definitions/1"))
                .andExpect(status().isOk());

        // 验证结果
        verify(taskDefinitionService, times(1)).removeById(anyLong());
    }

    @Test
    void testGetTaskDefinitionByName() throws Exception {
        // 准备测试数据
        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setId(1L);
        taskDefinition.setName("test-task");

        // 模拟方法调用
        when(taskDefinitionService.getByName(anyString())).thenReturn(taskDefinition);

        // 执行测试
        mockMvc.perform(get("/api/task-definitions/name/test-task"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test-task"));

        // 验证结果
        verify(taskDefinitionService, times(1)).getByName(anyString());
    }

    @Test
    void testGetTaskDefinitionsByClientId() throws Exception {
        // 准备测试数据
        TaskDefinition taskDefinition1 = new TaskDefinition();
        taskDefinition1.setId(1L);
        taskDefinition1.setClientId(1L);

        TaskDefinition taskDefinition2 = new TaskDefinition();
        taskDefinition2.setId(2L);
        taskDefinition2.setClientId(1L);

        List<TaskDefinition> taskDefinitionList = new ArrayList<>();
        taskDefinitionList.add(taskDefinition1);
        taskDefinitionList.add(taskDefinition2);

        // 模拟方法调用
        when(taskDefinitionService.getByClientId(anyLong())).thenReturn(taskDefinitionList);

        // 执行测试
        mockMvc.perform(get("/api/task-definitions/client/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        // 验证结果
        verify(taskDefinitionService, times(1)).getByClientId(anyLong());
    }
}