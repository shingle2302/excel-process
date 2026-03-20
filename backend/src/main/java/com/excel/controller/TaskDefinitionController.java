package com.excel.controller;

import com.excel.annotation.OperationLog;
import com.excel.annotation.RequirePermission;
import com.excel.entity.TaskDefinition;
import com.excel.service.TaskDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/task-definitions")
@Tag(name = "任务定义管理")
public class TaskDefinitionController {

    @Autowired
    private TaskDefinitionService taskDefinitionService;

    @PostMapping
    @RequirePermission("task-definition:write")
    @OperationLog(type = "任务定义", value = "创建任务定义")
    @Operation(summary = "创建任务定义")
    public TaskDefinition createTaskDefinition(@RequestBody TaskDefinition taskDefinition) {
        taskDefinitionService.save(taskDefinition);
        return taskDefinition;
    }

    @GetMapping
    @RequirePermission("task-definition:read")
    @Operation(summary = "获取任务定义列表")
    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitionService.list();
    }

    @GetMapping("/{id}")
    @RequirePermission("task-definition:read")
    @Operation(summary = "获取任务定义详情")
    public TaskDefinition getTaskDefinition(@PathVariable Long id) {
        return taskDefinitionService.getById(id);
    }

    @PutMapping("/{id}")
    @RequirePermission("task-definition:write")
    @OperationLog(type = "任务定义", value = "更新任务定义")
    @Operation(summary = "更新任务定义")
    public TaskDefinition updateTaskDefinition(@PathVariable Long id, @RequestBody TaskDefinition taskDefinition) {
        taskDefinition.setId(id);
        taskDefinitionService.updateById(taskDefinition);
        return taskDefinition;
    }

    @DeleteMapping("/{id}")
    @RequirePermission("task-definition:write")
    @OperationLog(type = "任务定义", value = "删除任务定义")
    @Operation(summary = "删除任务定义")
    public void deleteTaskDefinition(@PathVariable Long id) {
        taskDefinitionService.removeById(id);
    }

    @GetMapping("/name/{name}")
    @RequirePermission("task-definition:read")
    @Operation(summary = "按名称查询任务定义")
    public TaskDefinition getTaskDefinitionByName(@PathVariable String name) {
        return taskDefinitionService.getByName(name);
    }

    @GetMapping("/client/{clientId}")
    @RequirePermission("task-definition:read")
    @Operation(summary = "按客户端查询任务定义")
    public List<TaskDefinition> getTaskDefinitionsByClientId(@PathVariable Long clientId) {
        return taskDefinitionService.getByClientId(clientId);
    }
}
