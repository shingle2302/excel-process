package com.excel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.excel.service.ExcelService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Override
    public Map<String, Object> importExcel(MultipartFile file, List<Map<String, Object>> columnDefinitions) throws IOException {
        List<Map<String, Object>> dataList = parseExcel(file.getInputStream(), columnDefinitions);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", dataList);
        result.put("count", dataList.size());
        return result;
    }

    @Override
    public void exportExcel(List<Map<String, Object>> data, List<Map<String, Object>> columnDefinitions, OutputStream outputStream) throws IOException {
        // 构建表头
        List<String> headList = new ArrayList<>();
        List<String> fieldList = new ArrayList<>();
        for (Map<String, Object> column : columnDefinitions) {
            headList.add((String) column.get("columnName"));
            fieldList.add((String) column.get("fieldName"));
        }

        // 构建导出数据
        List<List<Object>> exportData = new ArrayList<>();
        for (Map<String, Object> row : data) {
            List<Object> rowData = new ArrayList<>();
            for (String field : fieldList) {
                rowData.add(row.get(field));
            }
            exportData.add(rowData);
        }

        // 导出Excel
        EasyExcel.write(outputStream).sheet("Sheet1").head(buildHead(headList)).doWrite(exportData);
    }

    @Override
    public List<Map<String, Object>> parseExcel(InputStream inputStream, List<Map<String, Object>> columnDefinitions) throws IOException {
        List<Map<String, Object>> dataList = new ArrayList<>();

        // 构建字段映射
        Map<Integer, String> indexToFieldMap = new HashMap<>();
        for (int i = 0; i < columnDefinitions.size(); i++) {
            indexToFieldMap.put(i, (String) columnDefinitions.get(i).get("fieldName"));
        }

        // 解析Excel
        EasyExcel.read(inputStream, new AnalysisEventListener<List<Object>>() {
            @Override
            public void invoke(List<Object> row, AnalysisContext context) {
                Map<String, Object> rowData = new HashMap<>();
                for (int i = 0; i < row.size(); i++) {
                    String fieldName = indexToFieldMap.get(i);
                    if (fieldName != null) {
                        rowData.put(fieldName, row.get(i));
                    }
                }
                dataList.add(rowData);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 解析完成
            }
        }).sheet().doRead();

        return dataList;
    }

    /**
     * 构建表头
     */
    private List<List<String>> buildHead(List<String> headList) {
        List<List<String>> head = new ArrayList<>();
        for (String title : headList) {
            List<String> column = new ArrayList<>();
            column.add(title);
            head.add(column);
        }
        return head;
    }
}