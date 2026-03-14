package com.excel.service.impl;

import com.excel.entity.ColumnDefinition;
import com.excel.mapper.ColumnDefinitionMapper;
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

class ColumnDefinitionServiceImplTest {

    @Mock
    private ColumnDefinitionMapper columnDefinitionMapper;

    @InjectMocks
    private ColumnDefinitionServiceImpl columnDefinitionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置baseMapper
        ReflectionTestUtils.setField(columnDefinitionService, "baseMapper", columnDefinitionMapper);
    }

    @Test
    void testGetByTaskDefinitionId() {
        // 准备测试数据
        ColumnDefinition columnDefinition1 = new ColumnDefinition();
        columnDefinition1.setId(1L);
        columnDefinition1.setTaskDefinitionId(1L);

        ColumnDefinition columnDefinition2 = new ColumnDefinition();
        columnDefinition2.setId(2L);
        columnDefinition2.setTaskDefinitionId(1L);

        List<ColumnDefinition> columnDefinitionList = new ArrayList<>();
        columnDefinitionList.add(columnDefinition1);
        columnDefinitionList.add(columnDefinition2);

        // 模拟方法调用
        when(columnDefinitionMapper.selectList(any())).thenReturn(columnDefinitionList);

        // 调用测试方法
        List<ColumnDefinition> result = columnDefinitionService.getByTaskDefinitionId(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(columnDefinitionMapper, times(1)).selectList(any());
    }

    @Test
    void testDeleteByTaskDefinitionId() {
        // 模拟方法调用
        when(columnDefinitionMapper.delete(any())).thenReturn(2);

        // 调用测试方法
        columnDefinitionService.deleteByTaskDefinitionId(1L);

        // 验证结果
        verify(columnDefinitionMapper, times(1)).delete(any());
    }
}