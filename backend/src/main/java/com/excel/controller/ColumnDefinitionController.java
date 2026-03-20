package com.excel.controller;

import com.excel.annotation.OperationLog;
import com.excel.annotation.RequirePermission;
import com.excel.entity.ColumnDefinition;
import com.excel.service.ColumnDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/column-definitions")
@Tag(name = "列定义管理")
public class ColumnDefinitionController {

    @Autowired
    private ColumnDefinitionService columnDefinitionService;

    @PostMapping
    @RequirePermission("column-definition:write")
    @OperationLog(type = "列定义", value = "创建列定义")
    @Operation(summary = "创建列定义")
    public ColumnDefinition createColumnDefinition(@RequestBody ColumnDefinition columnDefinition) {
        columnDefinitionService.save(columnDefinition);
        return columnDefinition;
    }

    @GetMapping
    @RequirePermission("column-definition:read")
    @Operation(summary = "获取列定义列表")
    public List<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitionService.list();
    }

    @GetMapping("/{id}")
    @RequirePermission("column-definition:read")
    @Operation(summary = "获取列定义详情")
    public ColumnDefinition getColumnDefinition(@PathVariable Long id) {
        return columnDefinitionService.getById(id);
    }

    @PutMapping("/{id}")
    @RequirePermission("column-definition:write")
    @OperationLog(type = "列定义", value = "更新列定义")
    @Operation(summary = "更新列定义")
    public ColumnDefinition updateColumnDefinition(@PathVariable Long id, @RequestBody ColumnDefinition columnDefinition) {
        columnDefinition.setId(id);
        columnDefinitionService.updateById(columnDefinition);
        return columnDefinition;
    }

    @DeleteMapping("/{id}")
    @RequirePermission("column-definition:write")
    @OperationLog(type = "列定义", value = "删除列定义")
    @Operation(summary = "删除列定义")
    public void deleteColumnDefinition(@PathVariable Long id) {
        columnDefinitionService.removeById(id);
    }

    @GetMapping("/task-definition/{taskDefinitionId}")
    @RequirePermission("column-definition:read")
    @Operation(summary = "按任务定义ID查询列定义")
    public List<ColumnDefinition> getColumnDefinitionsByTaskDefinitionId(@PathVariable Long taskDefinitionId) {
        return columnDefinitionService.getByTaskDefinitionId(taskDefinitionId);
    }

    @DeleteMapping("/task-definition/{taskDefinitionId}")
    @RequirePermission("column-definition:write")
    @OperationLog(type = "列定义", value = "按任务定义删除列定义")
    @Operation(summary = "按任务定义ID删除列定义")
    public void deleteColumnDefinitionsByTaskDefinitionId(@PathVariable Long taskDefinitionId) {
        columnDefinitionService.deleteByTaskDefinitionId(taskDefinitionId);
    }
}
