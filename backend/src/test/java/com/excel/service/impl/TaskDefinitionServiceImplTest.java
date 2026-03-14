package com.excel.service.impl;

import com.excel.entity.TaskDefinition;
import com.excel.mapper.TaskDefinitionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskDefinitionServiceImplTest {

    @Mock
    private TaskDefinitionMapper taskDefinitionMapper;

    @InjectMocks
    private TaskDefinitionServiceImpl taskDefinitionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置baseMapper
        ReflectionTestUtils.setField(taskDefinitionService, "baseMapper", taskDefinitionMapper);
    }

    @Test
    void testGetByName() {
        // 准备测试数据
        TaskDefinition taskDefinition = new TaskDefinition();
        taskDefinition.setId(1L);
        taskDefinition.setName("test-task");

        // 模拟方法调用
        when(taskDefinitionMapper.selectOne(any())).thenReturn(taskDefinition);

        // 调用测试方法
        TaskDefinition result = taskDefinitionService.getByName("test-task");

        // 验证结果
        assertNotNull(result);
        assertEquals("test-task", result.getName());
        verify(taskDefinitionMapper, times(1)).selectOne(any());
    }

    @Test
    void testGetByClientId() {
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
        when(taskDefinitionMapper.selectList(any())).thenReturn(taskDefinitionList);

        // 调用测试方法
        List<TaskDefinition> result = taskDefinitionService.getByClientId(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(taskDefinitionMapper, times(1)).selectList(any());
    }
}