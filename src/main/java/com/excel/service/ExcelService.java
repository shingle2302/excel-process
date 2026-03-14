package com.excel.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface ExcelService {
    /**
     * 导入Excel文件
     * @param file Excel文件
     * @param columnDefinitions 列定义
     * @return 导入结果
     * @throws IOException 异常
     */
    Map<String, Object> importExcel(MultipartFile file, List<Map<String, Object>> columnDefinitions) throws IOException;

    /**
     * 导出Excel文件
     * @param data 数据列表
     * @param columnDefinitions 列定义
     * @param outputStream 输出流
     * @throws IOException 异常
     */
    void exportExcel(List<Map<String, Object>> data, List<Map<String, Object>> columnDefinitions, OutputStream outputStream) throws IOException;

    /**
     * 解析Excel文件
     * @param inputStream 输入流
     * @param columnDefinitions 列定义
     * @return 解析结果
     * @throws IOException 异常
     */
    List<Map<String, Object>> parseExcel(InputStream inputStream, List<Map<String, Object>> columnDefinitions) throws IOException;
}