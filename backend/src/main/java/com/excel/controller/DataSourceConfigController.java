package com.excel.controller;

import com.excel.dto.DataSourceConnectionTestResponse;
import com.excel.entity.DataSourceConfig;
import com.excel.service.DataSourceConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/data-sources")
public class DataSourceConfigController {

    @Autowired
    private DataSourceConfigService dataSourceConfigService;

    @PostMapping("/test-connection")
    public DataSourceConnectionTestResponse testConnection(@RequestBody DataSourceConfig dataSourceConfig) {
        dataSourceConfigService.validateConnection(dataSourceConfig);
        return new DataSourceConnectionTestResponse(true, "数据源连通性测试成功");
    }

    @PostMapping
    public DataSourceConfig createDataSource(@RequestBody DataSourceConfig dataSourceConfig) {
        return dataSourceConfigService.createDataSource(dataSourceConfig);
    }

    @GetMapping
    public List<DataSourceConfig> getDataSources() {
        return dataSourceConfigService.list();
    }

    @GetMapping("/{id}")
    public DataSourceConfig getDataSource(@PathVariable Long id) {
        return dataSourceConfigService.getById(id);
    }

    @PutMapping("/{id}")
    public DataSourceConfig updateDataSource(@PathVariable Long id, @RequestBody DataSourceConfig dataSourceConfig) {
        dataSourceConfig.setId(id);
        dataSourceConfig.setUpdateTime(LocalDateTime.now());
        dataSourceConfigService.updateById(dataSourceConfig);
        return dataSourceConfig;
    }

    @DeleteMapping("/{id}")
    public void deleteDataSource(@PathVariable Long id) {
        dataSourceConfigService.removeById(id);
    }
}
