package com.excel.config;

import com.excel.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ApiKeyAuthenticationFilterTest {

    @Mock
    private ClientService clientService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 手动注入apiKeyHeader参数
        apiKeyAuthenticationFilter = new ApiKeyAuthenticationFilter(clientService, "X-API-Key");
    }

    @Test
    void testDoFilterInternalWithValidApiKey() throws ServletException, IOException {
        // 准备测试数据
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", "test-client");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 模拟方法调用
        when(clientService.isActiveClient(anyString())).thenReturn(true);

        // 执行测试
        apiKeyAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(clientService, times(1)).isActiveClient(anyString());
        verify(filterChain, times(1)).doFilter(any(), any());
    }

    @Test
    void testDoFilterInternalWithInvalidApiKey() throws ServletException, IOException {
        // 准备测试数据
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", "invalid-client");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 模拟方法调用
        when(clientService.isActiveClient(anyString())).thenReturn(false);

        // 执行测试
        apiKeyAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(clientService, times(1)).isActiveClient(anyString());
        verify(filterChain, times(0)).doFilter(any(), any());
        assertEquals(401, response.getStatus());
    }

    @Test
    void testDoFilterInternalWithoutApiKey() throws ServletException, IOException {
        // 准备测试数据
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // 执行测试
        apiKeyAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // 验证结果
        verify(clientService, times(0)).isActiveClient(anyString());
        verify(filterChain, times(1)).doFilter(any(), any());
    }
}