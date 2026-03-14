package com.excel.controller;

import com.excel.dto.TaskPageResponse;
import com.excel.entity.Task;
import com.excel.entity.TaskDefinition;
import com.excel.service.TaskService;
import com.excel.service.TaskDefinitionService;
import com.excel.service.ClientService;
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

class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private TaskDefinitionService taskDefinitionService;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void testCreateTask() throws Exception {
        // 准备测试数据
        Task task = new Task();
        task.setId(1L);
        task.setName("测试任务");
        task.setTaskDefinitionId(1L);

        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setId(1L);

        // 模拟方法调用
        when(clientService.validateClient(anyString(), anyString())).thenReturn(true);
        when(taskDefinitionService.getById(anyLong())).thenReturn(taskDefinition);
        when(taskService.createTask(any(Task.class))).thenReturn(task);

        // 执行测试
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-API-Key", "test-client")
                .content("{\"name\": \"测试任务\", \"taskDefinitionId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("测试任务"));

        // 验证结果
        verify(clientService, times(1)).validateClient(anyString(), anyString());
        verify(taskDefinitionService, times(1)).getById(anyLong());
        verify(taskService, times(1)).createTask(any(Task.class));
    }

    @Test
    void testGetTasks() throws Exception {
        // 准备测试数据
        Task task1 = new Task();
        task1.setId(1L);
        task1.setName("测试任务1");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setName("测试任务2");

        List<Task> taskList = new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);

        // 模拟方法调用
        when(taskService.list()).thenReturn(taskList);

        // 执行测试
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        // 验证结果
        verify(taskService, times(1)).list();
    }

    @Test
    void testGetTask() throws Exception {
        // 准备测试数据
        Task task = new Task();
        task.setId(1L);
        task.setName("测试任务");

        // 模拟方法调用
        when(taskService.getById(anyLong())).thenReturn(task);

        // 执行测试
        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("测试任务"));

        // 验证结果
        verify(taskService, times(1)).getById(anyLong());
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        // 模拟方法调用
        doNothing().when(taskService).updateTaskStatus(anyLong(), anyString());

        // 执行测试
        mockMvc.perform(put("/api/tasks/1/status")
                .param("status", "处理中"))
                .andExpect(status().isOk());

        // 验证结果
        verify(taskService, times(1)).updateTaskStatus(anyLong(), anyString());
    }

    @Test
    void testUpdateTaskProgress() throws Exception {
        // 模拟方法调用
        doNothing().when(taskService).updateTaskProgress(anyLong(), anyInt());

        // 执行测试
        mockMvc.perform(put("/api/tasks/1/progress")
                .param("progress", "50"))
                .andExpect(status().isOk());

        // 验证结果
        verify(taskService, times(1)).updateTaskProgress(anyLong(), anyInt());
    }

    @Test
    void testDeleteTask() throws Exception {
        // 准备测试数据
        Task task = new Task();
        task.setId(1L);
        task.setStatus("已完成");

        // 模拟方法调用
        when(taskService.getById(anyLong())).thenReturn(task);
        doNothing().when(taskService).removeById(anyLong());

        // 执行测试
        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isOk());

        // 验证结果
        verify(taskService, times(1)).getById(anyLong());
        verify(taskService, times(1)).removeById(anyLong());
    }
    @Test
    void testQueryTasks() throws Exception {
        Task task = new Task();
        task.setId(100L);
        task.setName("分页任务");

        TaskPageResponse response = new TaskPageResponse();
        response.setRecords(List.of(task));
        response.setTotal(1);
        response.setPageNo(1);
        response.setPageSize(10);
        response.setTotalPages(1);

        when(taskService.queryTasks(any())).thenReturn(response);

        mockMvc.perform(get("/api/tasks/query")
                .param("keyword", "分页")
                .param("status", "处理中")
                .param("type", "导入")
                .param("pageNo", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.records[0].id").value(100));

        verify(taskService, times(1)).queryTasks(any());
    }

}