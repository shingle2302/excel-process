package com.excel.controller;

import com.excel.annotation.OperationLog;
import com.excel.annotation.RequirePermission;
import com.excel.entity.Task;
import com.excel.exception.BusinessException;
import com.excel.plugin.TaskProcessorPlugin;
import com.excel.service.PluginRegistryService;
import com.excel.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/plugins")
@RequiredArgsConstructor
@Tag(name = "插件系统")
public class PluginController {

    private final PluginRegistryService pluginRegistryService;
    private final TaskService taskService;

    @GetMapping
    @RequirePermission("plugin:read")
    @Operation(summary = "插件列表")
    public List<String> listPlugins() {
        return pluginRegistryService.listPluginNames();
    }

    @PostMapping("/process-task/{taskId}")
    @RequirePermission("plugin:execute")
    @OperationLog(type = "插件", value = "插件处理任务")
    @Operation(summary = "使用插件处理任务")
    public Map<String, String> processTask(@PathVariable Long taskId) {
        Task task = taskService.getById(taskId);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        TaskProcessorPlugin plugin = pluginRegistryService.resolve(task.getType());
        plugin.process(task);
        return Map.of("plugin", plugin.pluginName(), "result", "accepted");
    }
}
