package com.excel.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.excel.dto.TaskPageResponse;
import com.excel.dto.TaskQueryRequest;
import com.excel.entity.Task;
import com.excel.mapper.TaskMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置baseMapper
        ReflectionTestUtils.setField(taskService, "baseMapper", taskMapper);
    }

    @Test
    void testGetByStatus() {
        // 准备测试数据
        Task task1 = new Task();
        task1.setId(1L);
        task1.setStatus("待处理");

        Task task2 = new Task();
        task2.setId(2L);
        task2.setStatus("待处理");

        List<Task> taskList = new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);

        // 模拟方法调用
        when(taskMapper.selectList(any())).thenReturn(taskList);

        // 调用测试方法
        List<Task> result = taskService.getByStatus("待处理");

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskMapper, times(1)).selectList(any());
    }

    @Test
    void testGetByClientId() {
        // 准备测试数据
        Task task1 = new Task();
        task1.setId(1L);
        task1.setTaskDefinitionId(1L);

        Task task2 = new Task();
        task2.setId(2L);
        task2.setTaskDefinitionId(1L);

        List<Task> taskList = new ArrayList<>();
        taskList.add(task1);
        taskList.add(task2);

        // 模拟方法调用
        when(taskMapper.selectList(any())).thenReturn(taskList);

        // 调用测试方法
        List<Task> result = taskService.getByClientId(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskMapper, times(1)).selectList(any());
    }

    @Test
    void testCreateTask() {
        // 准备测试数据
        Task task = new Task();
        task.setName("测试任务");
        task.setDescription("测试任务描述");
        task.setType("导出");

        // 模拟方法调用
        when(taskMapper.insert(any(Task.class))).thenReturn(1);

        // 调用测试方法
        Task result = taskService.createTask(task);

        // 验证结果
        assertNotNull(result);
        assertEquals("待处理", result.getStatus());
        assertEquals(0, result.getProgress());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        verify(taskMapper, times(1)).insert(any(Task.class));
    }

    @Test
    void testUpdateTaskStatus() {
        // 准备测试数据
        Task task = new Task();
        task.setId(1L);

        // 模拟方法调用
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);

        // 调用测试方法
        taskService.updateTaskStatus(1L, "处理中");

        // 验证结果
        verify(taskMapper, times(1)).updateById(any(Task.class));
    }

    @Test
    void testUpdateTaskProgress() {
        // 准备测试数据
        Task task = new Task();
        task.setId(1L);

        // 模拟方法调用
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);

        // 调用测试方法
        taskService.updateTaskProgress(1L, 50);

        // 验证结果
        verify(taskMapper, times(1)).updateById(any(Task.class));
    }

    @Test
    void testQueryTasks() {
        Task task = new Task();
        task.setId(1L);
        task.setName("query-task");

        Page<Task> pageResult = new Page<>(1, 10);
        pageResult.setRecords(List.of(task));
        pageResult.setTotal(1);

        when(taskMapper.selectPage(any(Page.class), any())).thenReturn(pageResult);

        TaskQueryRequest request = new TaskQueryRequest();
        request.setKeyword("query");
        request.setStatus("处理中");
        request.setType("导入");
        request.setPageNo(1);
        request.setPageSize(10);

        TaskPageResponse result = taskService.queryTasks(request);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        verify(taskMapper, times(1)).selectPage(any(Page.class), any());
    }

    @Test
    void testGetRecentTasks() {
        Task task = new Task();
        task.setId(1L);

        Page<Task> pageResult = new Page<>(1, 3);
        pageResult.setRecords(List.of(task));

        when(taskMapper.selectPage(any(Page.class), any())).thenReturn(pageResult);

        List<Task> result = taskService.getRecentTasks(3);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskMapper, times(1)).selectPage(any(Page.class), any());
    }

}