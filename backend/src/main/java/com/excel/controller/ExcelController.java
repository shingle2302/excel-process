package com.excel.controller;

import com.excel.annotation.OperationLog;
import com.excel.annotation.RequirePermission;
import com.excel.entity.ColumnDefinition;
import com.excel.exception.BusinessException;
import com.excel.service.ColumnDefinitionService;
import com.excel.service.ExcelService;
import com.excel.service.StorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/excel")
@Tag(name = "Excel处理")
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
    @RequirePermission("excel:process")
    @OperationLog(type = "Excel", value = "导入Excel")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file,
                                           @RequestParam("taskDefinitionId") Long taskDefinitionId) throws IOException {
        List<ColumnDefinition> columnDefinitionList = columnDefinitionService.getByTaskDefinitionId(taskDefinitionId);
        List<Map<String, Object>> columnDefinitions = convertToColumnDefinitions(columnDefinitionList);
        return excelService.importExcel(file, columnDefinitions);
    }

    @PostMapping("/export")
    @RequirePermission("excel:process")
    @OperationLog(type = "Excel", value = "导出Excel")
    public void exportExcel(@RequestParam("data") String dataJson,
                            @RequestParam("taskDefinitionId") Long taskDefinitionId,
                            HttpServletResponse response) throws IOException {
        List<ColumnDefinition> columnDefinitionList = columnDefinitionService.getByTaskDefinitionId(taskDefinitionId);
        List<Map<String, Object>> columnDefinitions = convertToColumnDefinitions(columnDefinitionList);

        List<Map<String, Object>> data = parseData(dataJson);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=export.xlsx");

        try (OutputStream outputStream = response.getOutputStream()) {
            excelService.exportExcel(data, columnDefinitions, outputStream);
        }
    }

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

    private List<Map<String, Object>> parseData(String dataJson) {
        try {
            return objectMapper.readValue(dataJson, new TypeReference<List<Map<String, Object>>>() {
            });
        } catch (IOException e) {
            throw new BusinessException(400, "解析JSON数据失败: " + e.getMessage());
        }
    }
}
