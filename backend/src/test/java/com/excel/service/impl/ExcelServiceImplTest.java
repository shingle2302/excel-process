package com.excel.service.impl;

import com.excel.service.ExcelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExcelServiceImplTest {

    @InjectMocks
    private ExcelServiceImpl excelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testImportExcel() throws IOException {
        // 准备测试数据
        String content = "ID,Username,Email\n1,test,test@example.com";
        MockMultipartFile file = new MockMultipartFile("test.xlsx", content.getBytes());

        List<Map<String, Object>> columnDefinitions = new ArrayList<>();
        Map<String, Object> column1 = new HashMap<>();
        column1.put("fieldName", "id");
        column1.put("columnName", "ID");
        column1.put("columnType", "number");
        columnDefinitions.add(column1);

        Map<String, Object> column2 = new HashMap<>();
        column2.put("fieldName", "username");
        column2.put("columnName", "Username");
        column2.put("columnType", "string");
        columnDefinitions.add(column2);

        Map<String, Object> column3 = new HashMap<>();
        column3.put("fieldName", "email");
        column3.put("columnName", "Email");
        column3.put("columnType", "string");
        columnDefinitions.add(column3);

        // 调用测试方法
        Map<String, Object> result = excelService.importExcel(file, columnDefinitions);

        // 验证结果
        assertNotNull(result);
        assertTrue((Boolean) result.get("success"));
        assertNotNull(result.get("data"));
        assertEquals(1, ((List<?>) result.get("data")).size());
    }

    @Test
    void testExportExcel() throws IOException {
        // 准备测试数据
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1);
        row.put("username", "test");
        row.put("email", "test@example.com");
        data.add(row);

        List<Map<String, Object>> columnDefinitions = new ArrayList<>();
        Map<String, Object> column1 = new HashMap<>();
        column1.put("fieldName", "id");
        column1.put("columnName", "ID");
        column1.put("columnType", "number");
        columnDefinitions.add(column1);

        Map<String, Object> column2 = new HashMap<>();
        column2.put("fieldName", "username");
        column2.put("columnName", "Username");
        column2.put("columnType", "string");
        columnDefinitions.add(column2);

        Map<String, Object> column3 = new HashMap<>();
        column3.put("fieldName", "email");
        column3.put("columnName", "Email");
        column3.put("columnType", "string");
        columnDefinitions.add(column3);

        // 创建输出流
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 调用测试方法
        excelService.exportExcel(data, columnDefinitions, outputStream);

        // 验证结果
        assertNotNull(outputStream);
        assertTrue(outputStream.size() > 0);
    }

    @Test
    void testParseExcel() throws IOException {
        // 准备测试数据 - 使用真实的Excel格式
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        List<List<String>> head = new ArrayList<>();
        head.add(List.of("ID"));
        head.add(List.of("Username"));
        head.add(List.of("Email"));

        List<List<Object>> data = new ArrayList<>();
        data.add(List.of(1, "test", "test@example.com"));

        com.alibaba.excel.EasyExcel.write(outputStream)
            .sheet("Sheet1")
            .head(buildHead(head))
            .doWrite(data);

        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        List<Map<String, Object>> columnDefinitions = new ArrayList<>();
        Map<String, Object> column1 = new HashMap<>();
        column1.put("fieldName", "id");
        column1.put("columnName", "ID");
        column1.put("columnType", "number");
        columnDefinitions.add(column1);

        Map<String, Object> column2 = new HashMap<>();
        column2.put("fieldName", "username");
        column2.put("columnName", "Username");
        column2.put("columnType", "string");
        columnDefinitions.add(column2);

        Map<String, Object> column3 = new HashMap<>();
        column3.put("fieldName", "email");
        column3.put("columnName", "Email");
        column3.put("columnType", "string");
        columnDefinitions.add(column3);

        // 调用测试方法
        List<Map<String, Object>> result = excelService.parseExcel(inputStream, columnDefinitions);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).get("id"));
        assertNotNull(result.get(0).get("username"));
        assertNotNull(result.get(0).get("email"));
    }

    private List<List<String>> buildHead(List<List<String>> headList) {
        return headList;
    }
}