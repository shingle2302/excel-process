package com.excel.controller;

import com.excel.entity.Client;
import com.excel.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(clientController).build();
    }

    @Test
    void testCreateClient() throws Exception {
        // 准备测试数据
        Client client = new Client();
        client.setId(1L);
        client.setClientId("test-client");
        client.setClientSecret("test-secret");
        client.setClientName("测试客户端");

        // 模拟方法调用
        when(clientService.createClient(any(Client.class))).thenReturn(client);

        // 执行测试
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clientId\": \"test-client\", \"clientSecret\": \"test-secret\", \"clientName\": \"测试客户端\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value("test-client"))
                .andExpect(jsonPath("$.clientName").value("测试客户端"));

        // 验证结果
        verify(clientService, times(1)).createClient(any(Client.class));
    }

    @Test
    void testGetClients() throws Exception {
        // 准备测试数据
        Client client1 = new Client();
        client1.setId(1L);
        client1.setClientId("test-client1");

        Client client2 = new Client();
        client2.setId(2L);
        client2.setClientId("test-client2");

        List<Client> clientList = new ArrayList<>();
        clientList.add(client1);
        clientList.add(client2);

        // 模拟方法调用
        when(clientService.list()).thenReturn(clientList);

        // 执行测试
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));

        // 验证结果
        verify(clientService, times(1)).list();
    }

    @Test
    void testGetClient() throws Exception {
        // 准备测试数据
        Client client = new Client();
        client.setId(1L);
        client.setClientId("test-client");

        // 模拟方法调用
        when(clientService.getById(anyLong())).thenReturn(client);

        // 执行测试
        mockMvc.perform(get("/api/clients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value("test-client"));

        // 验证结果
        verify(clientService, times(1)).getById(anyLong());
    }

    @Test
    void testUpdateClient() throws Exception {
        // 准备测试数据
        Client client = new Client();
        client.setId(1L);
        client.setClientId("test-client");
        client.setClientName("更新后的客户端");

        // 模拟方法调用
        doNothing().when(clientService).updateById(any(Client.class));

        // 执行测试
        mockMvc.perform(put("/api/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clientId\": \"test-client\", \"clientName\": \"更新后的客户端\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientName").value("更新后的客户端"));

        // 验证结果
        verify(clientService, times(1)).updateById(any(Client.class));
    }

    @Test
    void testDeleteClient() throws Exception {
        // 模拟方法调用
        doNothing().when(clientService).removeById(anyLong());

        // 执行测试
        mockMvc.perform(delete("/api/clients/1"))
                .andExpect(status().isOk());

        // 验证结果
        verify(clientService, times(1)).removeById(anyLong());
    }

    @Test
    void testGetClientByClientId() throws Exception {
        // 准备测试数据
        Client client = new Client();
        client.setId(1L);
        client.setClientId("test-client");

        // 模拟方法调用
        when(clientService.getByClientId(anyString())).thenReturn(Optional.of(client));

        // 执行测试
        mockMvc.perform(get("/api/clients/client-id/test-client"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.clientId").value("test-client"));

        // 验证结果
        verify(clientService, times(1)).getByClientId(anyString());
    }

    @Test
    void testUpdateClientStatus() throws Exception {
        // 模拟方法调用
        doNothing().when(clientService).updateClientStatus(anyString(), anyString());

        // 执行测试
        mockMvc.perform(put("/api/clients/client-id/test-client/status")
                .param("status", "禁用"))
                .andExpect(status().isOk());

        // 验证结果
        verify(clientService, times(1)).updateClientStatus(anyString(), anyString());
    }
}