package com.excel.controller;

import com.excel.dto.ExternalTaskCreateRequest;
import com.excel.dto.ExternalTaskCreateResponse;
import com.excel.dto.TaskPageResponse;
import com.excel.dto.TaskQueryRequest;
import com.excel.entity.Client;
import com.excel.entity.Task;
import com.excel.entity.TaskDefinition;
import com.excel.service.ClientService;
import com.excel.service.TaskDefinitionService;
import com.excel.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDefinitionService taskDefinitionService;

    @Autowired
    private ClientService clientService;

    @PostMapping
    public Task createTask(@RequestBody Task task, @RequestHeader("X-API-Key") String apiKey) {
        if (!clientService.isActiveClient(apiKey)) {
            throw new RuntimeException("Invalid client");
        }

        validateTaskDefinition(task, null);
        return taskService.createTask(task);
    }

    @PostMapping("/external")
    public ExternalTaskCreateResponse createExternalTask(@RequestBody ExternalTaskCreateRequest request) {
        boolean valid = clientService.validateClient(request.getClientId(), request.getClientSecret());
        if (!valid) {
            throw new RuntimeException("客户端认证失败");
        }

        Client client = clientService.getByClientId(request.getClientId())
                .orElseThrow(() -> new RuntimeException("客户端不存在"));

        Task task = new Task();
        task.setTaskDefinitionId(request.getTaskDefinitionId());
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setType(request.getType());
        task.setDataFetchType(request.getDataFetchType());
        task.setDataSourceId(request.getDataSourceId());
        task.setQuerySql(request.getQuerySql());
        task.setHttpMethod(request.getHttpMethod());
        task.setHttpUrl(request.getHttpUrl());
        task.setHttpNeedAuth(request.getHttpNeedAuth());
        task.setAuthUrl(request.getAuthUrl());
        task.setAuthParams(request.getAuthParams());
        task.setRequestParams(request.getRequestParams());
        task.setParams(request.getParams());

        validateTaskDefinition(task, client.getId());

        Task createdTask = taskService.createTask(task);
        return new ExternalTaskCreateResponse(client.getClientId(), client.getClientName(), createdTask);
    }

    private void validateTaskDefinition(Task task, Long clientId) {
        if (task.getTaskDefinitionId() != null) {
            TaskDefinition taskDefinition = taskDefinitionService.getById(task.getTaskDefinitionId());
            if (taskDefinition == null) {
                throw new RuntimeException("Task definition not found");
            }
            if (clientId != null && taskDefinition.getClientId() != null && !taskDefinition.getClientId().equals(clientId)) {
                throw new RuntimeException("任务定义不属于当前客户端");
            }
        }
    }

    @GetMapping
    public List<Task> getTasks(@RequestParam(required = false) String status) {
        if (status != null) {
            return taskService.getByStatus(status);
        }
        return taskService.list();
    }

    @GetMapping("/query")
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
    public Task getTask(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @PutMapping("/{id}/status")
    public void updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        taskService.updateTaskStatus(id, status);
    }

    @PutMapping("/{id}/progress")
    public void updateTaskProgress(@PathVariable Long id, @RequestParam Integer progress) {
        taskService.updateTaskProgress(id, progress);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        Task task = taskService.getById(id);
        if (task != null && "已完成".equals(task.getStatus())) {
            taskService.removeById(id);
        } else {
            throw new RuntimeException("Only completed tasks can be deleted");
        }
    }
}
