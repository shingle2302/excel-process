package com.excel.controller;

import com.excel.annotation.OperationLog;
import com.excel.annotation.RequirePermission;
import com.excel.dto.BatchTaskStatusUpdateRequest;
import com.excel.dto.ExternalTaskCreateRequest;
import com.excel.dto.ExternalTaskCreateResponse;
import com.excel.dto.TaskPageResponse;
import com.excel.dto.TaskQueryRequest;
import com.excel.entity.Client;
import com.excel.entity.Task;
import com.excel.entity.TaskDefinition;
import com.excel.exception.BusinessException;
import com.excel.service.AsyncTaskProcessingService;
import com.excel.service.ClientService;
import com.excel.service.TaskDefinitionService;
import com.excel.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "任务管理", description = "任务相关的API接口")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDefinitionService taskDefinitionService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AsyncTaskProcessingService asyncTaskProcessingService;

    @PostMapping
    @RequirePermission("task:write")
    @OperationLog(type = "任务", value = "创建任务")
    @Operation(summary = "创建任务")
    public Task createTask(@RequestBody Task task, @RequestHeader("X-API-Key") String apiKey) {
        if (!clientService.isActiveClient(apiKey)) {
            throw new BusinessException(401, "客户端未启用或不存在");
        }

        mergeTaskDefinitionConfig(task, null);
        return taskService.createTask(task);
    }

    @PostMapping("/external")
    @Operation(summary = "外部系统创建任务")
    public ExternalTaskCreateResponse createExternalTask(@RequestBody ExternalTaskCreateRequest request) {
        boolean valid = clientService.validateClient(request.getClientId(), request.getClientSecret());
        if (!valid) {
            throw new BusinessException(401, "客户端认证失败");
        }

        Client client = clientService.getByClientId(request.getClientId())
                .orElseThrow(() -> new BusinessException(404, "客户端不存在"));

        Task task = new Task();
        task.setTaskDefinitionId(request.getTaskDefinitionId());
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setType(request.getType());
        task.setRequestParams(request.getRequestParams());
        task.setParams(request.getParams());

        mergeTaskDefinitionConfig(task, client.getId());

        Task createdTask = taskService.createTask(task);
        return new ExternalTaskCreateResponse(client.getClientId(), client.getClientName(), createdTask);
    }

    private void mergeTaskDefinitionConfig(Task task, Long clientId) {
        if (task.getTaskDefinitionId() == null) {
            return;
        }
        TaskDefinition taskDefinition = taskDefinitionService.getById(task.getTaskDefinitionId());
        if (taskDefinition == null) {
            throw new BusinessException(404, "任务定义不存在");
        }
        if (clientId != null && taskDefinition.getClientId() != null && !taskDefinition.getClientId().equals(clientId)) {
            throw new BusinessException(403, "任务定义不属于当前客户端");
        }

        if (task.getType() == null) {
            task.setType(taskDefinition.getType());
        }
        task.setDataFetchType(taskDefinition.getDataFetchType());
        task.setDataSourceId(taskDefinition.getDataSourceId());
        task.setQuerySql(taskDefinition.getQuerySql());
        task.setHttpMethod(taskDefinition.getHttpMethod());
        task.setHttpUrl(taskDefinition.getHttpUrl());
        task.setHttpNeedAuth(taskDefinition.getHttpNeedAuth());
        task.setAuthUrl(taskDefinition.getAuthUrl());
        task.setAuthParams(taskDefinition.getAuthParams());

        if (task.getRequestParams() == null) {
            task.setRequestParams(taskDefinition.getRequestParams());
        }
        if (task.getParams() == null) {
            task.setParams(taskDefinition.getParams());
        }
    }

    @GetMapping
    @RequirePermission("task:read")
    public List<Task> getTasks(@RequestParam(required = false) String status) {
        if (status != null) {
            return taskService.getByStatus(status);
        }
        return taskService.list();
    }

    @GetMapping("/query")
    @RequirePermission("task:read")
    public TaskPageResponse queryTasks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        TaskQueryRequest request = new TaskQueryRequest();
        request.setKeyword(keyword);
        request.setStatus(status);
        request.setType(type);
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);
        return taskService.queryTasks(request);
    }

    @GetMapping("/{id}")
    @RequirePermission("task:read")
    public Task getTask(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PutMapping("/{id}/status")
    @RequirePermission("task:write")
    @OperationLog(type = "任务", value = "更新任务状态")
    public void updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        taskService.updateTaskStatus(id, status);
    }

    @PutMapping("/batch/status")
    @RequirePermission("task:write")
    @OperationLog(type = "任务", value = "批量更新任务状态")
    @Operation(summary = "批量更新任务状态")
    public Map<String, Object> batchUpdateTaskStatus(@RequestBody BatchTaskStatusUpdateRequest request) {
        taskService.batchUpdateStatus(request.getTaskIds(), request.getStatus());
        return Map.of("updated", request.getTaskIds() == null ? 0 : request.getTaskIds().size(), "status", request.getStatus());
    }

    @PutMapping("/{id}/progress")
    @RequirePermission("task:write")
    @OperationLog(type = "任务", value = "更新任务进度")
    public void updateTaskProgress(@PathVariable Long id, @RequestParam Integer progress) {
        taskService.updateTaskProgress(id, progress);
    }

    @PostMapping("/{id}/process-async")
    @RequirePermission("task:write")
    @OperationLog(type = "任务", value = "异步处理任务")
    @Operation(summary = "异步处理任务")
    public Map<String, Object> processTaskAsync(@PathVariable Long id) {
        asyncTaskProcessingService.processTaskAsync(id);
        return Map.of("taskId", id, "accepted", true, "message", "任务已提交异步执行");
    }

    @DeleteMapping("/{id}")
    @RequirePermission("task:write")
    @OperationLog(type = "任务", value = "删除任务")
    public void deleteTask(@PathVariable Long id) {
        Task task = taskService.getById(id);
        if (task != null && "已完成".equals(task.getStatus())) {
            taskService.removeById(id);
        } else {
            throw new BusinessException(400, "仅允许删除已完成任务");
        }
    }
}
