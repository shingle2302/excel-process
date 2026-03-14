package com.excel.controller;

import com.excel.entity.ColumnDefinition;
import com.excel.service.ExcelService;
import com.excel.service.ColumnDefinitionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ExcelControllerTest {

    @Mock
    private ExcelService excelService;

    @Mock
    private ColumnDefinitionService columnDefinitionService;

    @InjectMocks
    private ExcelController excelController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(excelController).build();
    }

    @Test
    void testImportExcel() throws Exception {
        // 准备测试数据
        String content = "ID,Username,Email\n1,test,test@example.com";
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content.getBytes());

        ColumnDefinition columnDefinition1 = new ColumnDefinition();
        columnDefinition1.setFieldName("id");
        columnDefinition1.setColumnName("ID");
        columnDefinition1.setColumnType("number");

        ColumnDefinition columnDefinition2 = new ColumnDefinition();
        columnDefinition2.setFieldName("username");
        columnDefinition2.setColumnName("Username");
        columnDefinition2.setColumnType("string");

        List<ColumnDefinition> columnDefinitionList = new ArrayList<>();
        columnDefinitionList.add(columnDefinition1);
        columnDefinitionList.add(columnDefinition2);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("count", 1);

        // 模拟方法调用
        when(columnDefinitionService.getByTaskDefinitionId(anyLong())).thenReturn(columnDefinitionList);
        when(excelService.importExcel(any(), anyList())).thenReturn(result);

        // 执行测试
        mockMvc.perform(multipart("/api/excel/import")
                .file(file)
                .param("taskDefinitionId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.count").value(1));

        // 验证结果
        verify(columnDefinitionService, times(1)).getByTaskDefinitionId(anyLong());
        verify(excelService, times(1)).importExcel(any(), anyList());
    }

    @Test
    void testExportExcel() throws Exception {
        // 准备测试数据
        ColumnDefinition columnDefinition1 = new ColumnDefinition();
        columnDefinition1.setFieldName("id");
        columnDefinition1.setColumnName("ID");
        columnDefinition1.setColumnType("number");

        ColumnDefinition columnDefinition2 = new ColumnDefinition();
        columnDefinition2.setFieldName("username");
        columnDefinition2.setColumnName("Username");
        columnDefinition2.setColumnType("string");

        List<ColumnDefinition> columnDefinitionList = new ArrayList<>();
        columnDefinitionList.add(columnDefinition1);
        columnDefinitionList.add(columnDefinition2);

        // 模拟方法调用
        when(columnDefinitionService.getByTaskDefinitionId(anyLong())).thenReturn(columnDefinitionList);
        doNothing().when(excelService).exportExcel(anyList(), anyList(), any());

        // 执行测试
        mockMvc.perform(post("/api/excel/export")
                .param("data", "[{\"id\": 1, \"username\": \"test\"}]")
                .param("taskDefinitionId", "1"))
                .andExpect(status().isOk());

        // 验证结果
        verify(columnDefinitionService, times(1)).getByTaskDefinitionId(anyLong());
        verify(excelService, times(1)).exportExcel(anyList(), anyList(), any());
    }
}