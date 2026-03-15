package com.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excel.entity.DataSourceConfig;
import com.excel.mapper.DataSourceConfigMapper;
import com.excel.service.DataSourceConfigService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DataSourceConfigServiceImpl extends ServiceImpl<DataSourceConfigMapper, DataSourceConfig> implements DataSourceConfigService {

    @Override
    public DataSourceConfig createDataSource(DataSourceConfig dataSourceConfig) {
        dataSourceConfig.setCreateTime(LocalDateTime.now());
        dataSourceConfig.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(dataSourceConfig);
        return dataSourceConfig;
    }
}
