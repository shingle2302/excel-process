package com.excel.controller;

import com.excel.annotation.OperationLog;
import com.excel.annotation.RequirePermission;
import com.excel.dto.DataSourceConnectionTestResponse;
import com.excel.entity.DataSourceConfig;
import com.excel.service.DataSourceConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/data-sources")
@Tag(name = "数据源管理")
public class DataSourceConfigController {

    @Autowired
    private DataSourceConfigService dataSourceConfigService;

    @PostMapping("/test-connection")
    @RequirePermission("datasource:write")
    @Operation(summary = "测试数据源连通性")
    public DataSourceConnectionTestResponse testConnection(@RequestBody DataSourceConfig dataSourceConfig) {
        dataSourceConfigService.validateConnection(dataSourceConfig);
        return new DataSourceConnectionTestResponse(true, "数据源连通性测试成功");
    }

    @PostMapping
    @RequirePermission("datasource:write")
    @OperationLog(type = "数据源", value = "创建数据源")
    @Operation(summary = "创建数据源")
    public DataSourceConfig createDataSource(@RequestBody DataSourceConfig dataSourceConfig) {
        return dataSourceConfigService.createDataSource(dataSourceConfig);
    }

    @GetMapping
    @RequirePermission("datasource:read")
    @Operation(summary = "获取数据源列表")
    public List<DataSourceConfig> getDataSources() {
        return dataSourceConfigService.list();
    }

    @GetMapping("/{id}")
    @RequirePermission("datasource:read")
    @Operation(summary = "获取数据源详情")
    public DataSourceConfig getDataSource(@PathVariable Long id) {
        return dataSourceConfigService.getById(id);
    }

    @PutMapping("/{id}")
    @RequirePermission("datasource:write")
    @OperationLog(type = "数据源", value = "更新数据源")
    @Operation(summary = "更新数据源")
    public DataSourceConfig updateDataSource(@PathVariable Long id, @RequestBody DataSourceConfig dataSourceConfig) {
        dataSourceConfig.setId(id);
        dataSourceConfig.setUpdateTime(LocalDateTime.now());
        dataSourceConfigService.updateById(dataSourceConfig);
        return dataSourceConfig;
    }

    @DeleteMapping("/{id}")
    @RequirePermission("datasource:write")
    @OperationLog(type = "数据源", value = "删除数据源")
    @Operation(summary = "删除数据源")
    public void deleteDataSource(@PathVariable Long id) {
        dataSourceConfigService.removeById(id);
    }
}
