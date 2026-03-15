package com.excel.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.excel.entity.ColumnDefinition;
import com.excel.service.ExcelService;
import com.excel.service.StorageService;
import com.excel.service.ColumnDefinitionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private ColumnDefinitionService columnDefinitionService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/import")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file, 
                                           @RequestParam("taskDefinitionId") Long taskDefinitionId) throws IOException {
        // 根据任务定义查询列定义
        List<ColumnDefinition> columnDefinitionList = columnDefinitionService.getByTaskDefinitionId(taskDefinitionId);
        List<Map<String, Object>> columnDefinitions = convertToColumnDefinitions(columnDefinitionList);
        return excelService.importExcel(file, columnDefinitions);
    }

    @PostMapping("/export")
    public void exportExcel(@RequestParam("data") String dataJson, 
                           @RequestParam("taskDefinitionId") Long taskDefinitionId, 
                           HttpServletResponse response) throws IOException {
        // 根据任务定义查询列定义
        List<ColumnDefinition> columnDefinitionList = columnDefinitionService.getByTaskDefinitionId(taskDefinitionId);
        List<Map<String, Object>> columnDefinitions = convertToColumnDefinitions(columnDefinitionList);

        // 解析数据
        List<Map<String, Object>> data = parseData(dataJson);

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=export.xlsx");

        // 导出Excel
        try (OutputStream outputStream = response.getOutputStream()) {
            excelService.exportExcel(data, columnDefinitions, outputStream);
        }
    }

    /**
     * 将ColumnDefinition转换为Map格式
     */
    private List<Map<String, Object>> convertToColumnDefinitions(List<ColumnDefinition> columnDefinitionList) {
        List<Map<String, Object>> columnDefinitions = new ArrayList<>();
        for (ColumnDefinition columnDefinition : columnDefinitionList) {
            Map<String, Object> column = new HashMap<>();
            column.put("fieldName", columnDefinition.getFieldName());
            column.put("columnName", columnDefinition.getColumnName());
            column.put("columnType", columnDefinition.getColumnType());
            column.put("columnFormat", columnDefinition.getColumnFormat());
            columnDefinitions.add(column);
        }
        return columnDefinitions;
    }

    /**
     * 解析数据
     */
    private List<Map<String, Object>> parseData(String dataJson) {
        try {
            return objectMapper.readValue(dataJson, new TypeReference<List<Map<String, Object>>>() {});
        } catch (IOException e) {
            throw new RuntimeException("解析JSON数据失败: " + e.getMessage(), e);
        }
    }
}