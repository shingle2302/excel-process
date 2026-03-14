package com.excel.controller;

import com.excel.entity.ColumnDefinition;
import com.excel.service.ColumnDefinitionService;
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

class ColumnDefinitionControllerTest {

    @Mock
    private ColumnDefinitionService columnDefinitionService;

    @InjectMocks
    private ColumnDefinitionController columnDefinitionController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(columnDefinitionController).build();
    }

    @Test
    void testCreateColumnDefinition() throws Exception {
        // 准备测试数据
        ColumnDefinition columnDefinition = new ColumnDefinition();
        columnDefinition.setId(1L);
        columnDefinition.setTaskDefinitionId(1L);
        columnDefinition.setFieldName("id");
        columnDefinition.setColumnName("ID");
        columnDefinition.setColumnType("number");

        // 模拟方法调用
        doNothing().when(columnDefinitionService).save(any(ColumnDefinition.class));

        // 执行测试
        mockMvc.perform(post("/api/column-definitions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"taskDefinitionId\": 1, \"fieldName\": \"id\", \"columnName\": \"ID\", \"columnType\": \"number\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fieldName").value("id"))
                .andExpect(jsonPath("$.columnName").value("ID"));

        // 验证结果
        verify(columnDefinitionService, times(1)).save(any(ColumnDefinition.class));
    }

    @Test
    void testGetColumnDefinitions() throws Exception {
        // 准备测试数据
        ColumnDefinition columnDefinition1 = new ColumnDefinition();
        columnDefinition1.setId(1L);
        columnDefinition1.setFieldName("id");

        ColumnDefinition columnDefinition2 = new ColumnDefinition();
        columnDefinition2.setId(2L);
        columnDefinition2.setFieldName("username");

        List<ColumnDefinition> columnDefinitionList = new ArrayList<>();
        columnDefinitionList.add(columnDefinition1);
        columnDefinitionList.add(columnDefinition2);

        // 模拟方法调用
        when(columnDefinitionService.list()).thenReturn(columnDefinitionList);

        // 执行测试
        mockMvc.perform(get("/api/column-definitions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        // 验证结果
        verify(columnDefinitionService, times(1)).list();
    }

    @Test
    void testGetColumnDefinition() throws Exception {
        // 准备测试数据
        ColumnDefinition columnDefinition = new ColumnDefinition();
        columnDefinition.setId(1L);
        columnDefinition.setFieldName("id");
        columnDefinition.setColumnName("ID");

        // 模拟方法调用
        when(columnDefinitionService.getById(anyLong())).thenReturn(columnDefinition);

        // 执行测试
        mockMvc.perform(get("/api/column-definitions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.fieldName").value("id"))
                .andExpect(jsonPath("$.columnName").value("ID"));

        // 验证结果
        verify(columnDefinitionService, times(1)).getById(anyLong());
    }

    @Test
    void testUpdateColumnDefinition() throws Exception {
        // 准备测试数据
        ColumnDefinition columnDefinition = new ColumnDefinition();
        columnDefinition.setId(1L);
        columnDefinition.setFieldName("id");
        columnDefinition.setColumnName("用户ID");

        // 模拟方法调用
        doNothing().when(columnDefinitionService).updateById(any(ColumnDefinition.class));

        // 执行测试
        mockMvc.perform(put("/api/column-definitions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fieldName\": \"id\", \"columnName\": \"用户ID\", \"columnType\": \"number\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.columnName").value("用户ID"));

        // 验证结果
        verify(columnDefinitionService, times(1)).updateById(any(ColumnDefinition.class));
    }

    @Test
    void testDeleteColumnDefinition() throws Exception {
        // 模拟方法调用
        doNothing().when(columnDefinitionService).removeById(anyLong());

        // 执行测试
        mockMvc.perform(delete("/api/column-definitions/1"))
                .andExpect(status().isOk());

        // 验证结果
        verify(columnDefinitionService, times(1)).removeById(anyLong());
    }

    @Test
    void testGetColumnDefinitionsByTaskDefinitionId() throws Exception {
        // 准备测试数据
        ColumnDefinition columnDefinition1 = new ColumnDefinition();
        columnDefinition1.setId(1L);
        columnDefinition1.setTaskDefinitionId(1L);
        columnDefinition1.setFieldName("id");

        ColumnDefinition columnDefinition2 = new ColumnDefinition();
        columnDefinition2.setId(2L);
        columnDefinition2.setTaskDefinitionId(1L);
        columnDefinition2.setFieldName("username");

        List<ColumnDefinition> columnDefinitionList = new ArrayList<>();
        columnDefinitionList.add(columnDefinition1);
        columnDefinitionList.add(columnDefinition2);

        // 模拟方法调用
        when(columnDefinitionService.getByTaskDefinitionId(anyLong())).thenReturn(columnDefinitionList);

        // 执行测试
        mockMvc.perform(get("/api/column-definitions/task-definition/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        // 验证结果
        verify(columnDefinitionService, times(1)).getByTaskDefinitionId(anyLong());
    }

    @Test
    void testDeleteColumnDefinitionsByTaskDefinitionId() throws Exception {
        // 模拟方法调用
        doNothing().when(columnDefinitionService).deleteByTaskDefinitionId(anyLong());

        // 执行测试
        mockMvc.perform(delete("/api/column-definitions/task-definition/1"))
                .andExpect(status().isOk());

        // 验证结果
        verify(columnDefinitionService, times(1)).deleteByTaskDefinitionId(anyLong());
    }
}