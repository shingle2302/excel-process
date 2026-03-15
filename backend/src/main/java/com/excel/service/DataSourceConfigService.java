package com.excel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.excel.entity.DataSourceConfig;

public interface DataSourceConfigService extends IService<DataSourceConfig> {
    DataSourceConfig createDataSource(DataSourceConfig dataSourceConfig);
}
