package com.excel.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;

public interface StorageService {
    /**
     * 上传文件
     * @param file 文件
     * @param objectName 对象名
     * @return 文件路径
     * @throws Exception 异常
     */
    String uploadFile(MultipartFile file, String objectName) throws Exception;

    /**
     * 下载文件
     * @param objectName 对象名
     * @param outputStream 输出流
     * @throws Exception 异常
     */
    void downloadFile(String objectName, OutputStream outputStream) throws Exception;

    /**
     * 删除文件
     * @param objectName 对象名
     * @throws Exception 异常
     */
    void deleteFile(String objectName) throws Exception;

    /**
     * 获取文件输入流
     * @param objectName 对象名
     * @return 输入流
     * @throws Exception 异常
     */
    InputStream getFileInputStream(String objectName) throws Exception;
}