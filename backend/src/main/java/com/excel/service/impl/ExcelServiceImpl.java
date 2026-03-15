package com.excel.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.write.metadata.WriteSheet;
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

    private static final int EXPORT_BATCH_SIZE = 1000;

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
        List<String> headList = new ArrayList<>(columnDefinitions.size());
        List<String> fieldList = new ArrayList<>(columnDefinitions.size());
        for (Map<String, Object> column : columnDefinitions) {
            headList.add((String) column.get("columnName"));
            fieldList.add((String) column.get("fieldName"));
        }

        // 分批导出，避免大数据量时占用过多内存
        try (ExcelWriter excelWriter = EasyExcel.write(outputStream).head(buildHead(headList)).build()) {
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();
            List<List<Object>> batchData = new ArrayList<>(Math.min(data.size(), EXPORT_BATCH_SIZE));
            for (Map<String, Object> row : data) {
                List<Object> rowData = new ArrayList<>(fieldList.size());
                for (String field : fieldList) {
                    rowData.add(row.get(field));
                }
                batchData.add(rowData);

                if (batchData.size() >= EXPORT_BATCH_SIZE) {
                    excelWriter.write(batchData, writeSheet);
                    batchData = new ArrayList<>(Math.min(data.size(), EXPORT_BATCH_SIZE));
                }
            }

            if (!batchData.isEmpty()) {
                excelWriter.write(batchData, writeSheet);
            }
        }
    }

    @Override
    public List<Map<String, Object>> parseExcel(InputStream inputStream, List<Map<String, Object>> columnDefinitions) throws IOException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        // 构建字段映射
        Map<Integer, String> indexToFieldMap = new HashMap<>(columnDefinitions.size());
        for (int i = 0; i < columnDefinitions.size(); i++) {
            indexToFieldMap.put(i, (String) columnDefinitions.get(i).get("fieldName"));
        }

        // 构建列名到字段名的映射
        Map<String, String> columnNameToFieldMap = new HashMap<>(columnDefinitions.size());
        for (Map<String, Object> column : columnDefinitions) {
            columnNameToFieldMap.put((String) column.get("columnName"), (String) column.get("fieldName"));
        }

        // 解析Excel/CSV
        EasyExcel.read(inputStream, new AnalysisEventListener<Map<Integer, String>>() {
            private Map<Integer, String> resolvedIndexToFieldMap = indexToFieldMap;

            @Override
            public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
                // 基于表头计算列索引到字段的映射，避免每行重复做列名匹配
                Map<Integer, String> matchedFieldMap = new HashMap<>(headMap.size());
                for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                    String columnName = entry.getValue().getStringValue();
                    String fieldName = columnNameToFieldMap.get(columnName);
                    if (fieldName != null) {
                        matchedFieldMap.put(entry.getKey(), fieldName);
                    }
                }

                if (!matchedFieldMap.isEmpty()) {
                    resolvedIndexToFieldMap = matchedFieldMap;
                }
            }

            @Override
            public void invoke(Map<Integer, String> row, AnalysisContext context) {
                Map<String, Object> rowData = new HashMap<>(resolvedIndexToFieldMap.size());
                for (Map.Entry<Integer, String> entry : row.entrySet()) {
                    String fieldName = resolvedIndexToFieldMap.get(entry.getKey());
                    if (fieldName != null) {
                        rowData.put(fieldName, entry.getValue());
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
