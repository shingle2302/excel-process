package com.excel.service.impl;

import com.excel.entity.Client;
import com.excel.mapper.ClientMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceImplTest {

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 设置baseMapper
        ReflectionTestUtils.setField(clientService, "baseMapper", clientMapper);
    }

    @Test
    void testGetByClientId() {
        // 准备测试数据
        Client client = new Client();
        client.setId(1L);
        client.setClientId("test-client");

        // 模拟方法调用
        when(clientMapper.selectOne(any())).thenReturn(client);

        // 调用测试方法
        Optional<Client> result = clientService.getByClientId("test-client");

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("test-client", result.get().getClientId());
        verify(clientMapper, times(1)).selectOne(any());
    }

    @Test
    void testValidateClient() {
        // 准备测试数据
        Client client = new Client();
        client.setId(1L);
        client.setClientId("test-client");
        client.setClientSecret("test-secret");
        client.setStatus("启用");

        // 模拟方法调用
        when(clientMapper.selectOne(any())).thenReturn(client);

        // 调用测试方法
        boolean result = clientService.validateClient("test-client", "test-secret");

        // 验证结果
        assertTrue(result);
        verify(clientMapper, times(1)).selectOne(any());
    }

    @Test
    void testValidateClientInvalid() {
        // 准备测试数据
        Client client = new Client();
        client.setId(1L);
        client.setClientId("test-client");
        client.setClientSecret("test-secret");
        client.setStatus("禁用");

        // 模拟方法调用
        when(clientMapper.selectOne(any())).thenReturn(client);

        // 调用测试方法
        boolean result = clientService.validateClient("test-client", "test-secret");

        // 验证结果
        assertFalse(result);
        verify(clientMapper, times(1)).selectOne(any());
    }

    @Test
    void testCreateClient() {
        // 准备测试数据
        Client client = new Client();
        client.setClientId("test-client");
        client.setClientSecret("test-secret");
        client.setClientName("测试客户端");

        // 模拟方法调用
        when(clientMapper.insert(any(Client.class))).thenReturn(1);

        // 调用测试方法
        Client result = clientService.createClient(client);

        // 验证结果
        assertNotNull(result);
        assertEquals("启用", result.getStatus());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        verify(clientMapper, times(1)).insert(any(Client.class));
    }

    @Test
    void testUpdateClientStatus() {
        // 模拟方法调用
        when(clientMapper.update(any(Client.class), any())).thenReturn(1);

        // 调用测试方法
        clientService.updateClientStatus("test-client", "禁用");

        // 验证结果
        verify(clientMapper, times(1)).update(any(Client.class), any());
    }
    @Test
    void testIsActiveClient() {
        Client client = new Client();
        client.setClientId("admin");
        client.setStatus("启用");

        when(clientMapper.selectOne(any())).thenReturn(client);

        boolean result = clientService.isActiveClient("admin");

        assertTrue(result);
        verify(clientMapper, times(1)).selectOne(any());
    }

}