package com.excel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
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
        final Map<Integer, String>[] headerMapHolder = new Map[1];

        // 构建字段映射
        Map<Integer, String> indexToFieldMap = new HashMap<>();
        for (int i = 0; i < columnDefinitions.size(); i++) {
            indexToFieldMap.put(i, (String) columnDefinitions.get(i).get("fieldName"));
        }

        // 构建列名到字段名的映射
        Map<String, String> columnNameToFieldMap = new HashMap<>();
        for (Map<String, Object> column : columnDefinitions) {
            columnNameToFieldMap.put((String) column.get("columnName"), (String) column.get("fieldName"));
        }

        // 解析Excel/CSV
        EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
            @Override
            public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                // 保存表头信息
                Map<Integer, String> stringHeadMap = new HashMap<>();
                for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                    stringHeadMap.put(entry.getKey(), entry.getValue().getStringValue());
                }
                headerMapHolder[0] = stringHeadMap;
            }

            @Override
            public void invoke(Map<Integer, String> row, AnalysisContext context) {
                Map<String, Object> rowData = new HashMap<>();
                Map<Integer, String> headerMap = headerMapHolder[0];
                if (headerMap != null) {
                    // 使用表头映射
                    for (Map.Entry<Integer, String> entry : row.entrySet()) {
                        Integer index = entry.getKey();
                        String columnName = headerMap.get(index);
                        if (columnName != null) {
                            String fieldName = columnNameToFieldMap.get(columnName);
                            if (fieldName != null) {
                                rowData.put(fieldName, entry.getValue());
                            }
                        }
                    }
                } else {
                    // 使用索引映射
                    for (Map.Entry<Integer, String> entry : row.entrySet()) {
                        String fieldName = indexToFieldMap.get(entry.getKey());
                        if (fieldName != null) {
                            rowData.put(fieldName, entry.getValue());
                        }
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