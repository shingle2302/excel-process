package com.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excel.entity.DataSourceConfig;
import com.excel.exception.BusinessException;
import com.excel.mapper.DataSourceConfigMapper;
import com.excel.service.DataSourceConfigService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class DataSourceConfigServiceImpl extends ServiceImpl<DataSourceConfigMapper, DataSourceConfig> implements DataSourceConfigService {

    private final ObjectMapper objectMapper;

    public DataSourceConfigServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public DataSourceConfig createDataSource(DataSourceConfig dataSourceConfig) {
        validateConnection(dataSourceConfig);
        dataSourceConfig.setCreateTime(LocalDateTime.now());
        dataSourceConfig.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(dataSourceConfig);
        return dataSourceConfig;
    }

    @Override
    public void validateConnection(DataSourceConfig dataSourceConfig) {
        try {
            Map<String, Object> config = objectMapper.readValue(dataSourceConfig.getConnectionConfig(), new TypeReference<>() {
            });
            String host = String.valueOf(config.getOrDefault("host", "")).trim();
            if (host.isEmpty()) {
                throw new BusinessException(400, "连接配置缺少 host");
            }

            Object portObj = config.get("port");
            int port;
            if (portObj == null) {
                throw new BusinessException(400, "连接配置缺少 port");
            }
            if (portObj instanceof Number number) {
                port = number.intValue();
            } else {
                port = Integer.parseInt(String.valueOf(portObj));
            }

            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 3000);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(400, "数据源连通性测试失败: " + e.getMessage());
        }
    }
}
