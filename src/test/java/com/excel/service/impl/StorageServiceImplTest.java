package com.excel.service.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.GetObjectResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StorageServiceImplTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private StorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置bucketName
        ReflectionTestUtils.setField(storageService, "bucketName", "test-bucket");
    }

    @Test
    void testUploadFile() throws Exception {
        // 准备测试数据
        String content = "test content";
        MockMultipartFile file = new MockMultipartFile("test.txt", content.getBytes());
        String objectName = "test/test.txt";

        // 模拟方法调用
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        // 调用测试方法
        String result = storageService.uploadFile(file, objectName);

        // 验证结果
        assertEquals(objectName, result);
        verify(minioClient, times(1)).putObject(any(PutObjectArgs.class));
    }

    @Test
    void testDownloadFile() throws Exception {
        // 准备测试数据
        String objectName = "test/test.txt";
        String content = "test content";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 模拟GetObjectResponse
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);
        when(mockResponse.readAllBytes()).thenReturn(content.getBytes());

        // 模拟方法调用
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);

        // 调用测试方法
        storageService.downloadFile(objectName, outputStream);

        // 验证结果
        assertEquals(content, outputStream.toString());
        verify(minioClient, times(1)).getObject(any(GetObjectArgs.class));
    }

    @Test
    void testDeleteFile() throws Exception {
        // 准备测试数据
        String objectName = "test/test.txt";

        // 模拟方法调用
        doNothing().when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // 调用测试方法
        storageService.deleteFile(objectName);

        // 验证结果
        verify(minioClient, times(1)).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void testGetFileInputStream() throws Exception {
        // 准备测试数据
        String objectName = "test/test.txt";

        // 模拟GetObjectResponse
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);

        // 模拟方法调用
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);

        // 调用测试方法
        InputStream result = storageService.getFileInputStream(objectName);

        // 验证结果
        assertNotNull(result);
        verify(minioClient, times(1)).getObject(any(GetObjectArgs.class));
    }
}