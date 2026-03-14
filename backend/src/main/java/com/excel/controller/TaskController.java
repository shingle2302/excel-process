package com.excel.controller;

import com.excel.dto.TaskPageResponse;
import com.excel.dto.TaskQueryRequest;
import com.excel.entity.Task;
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
        if (!clientService.validateClient(apiKey, apiKey)) {
            throw new RuntimeException("Invalid client");
        }

        if (task.getTaskDefinitionId() != null) {
            var taskDefinition = taskDefinitionService.getById(task.getTaskDefinitionId());
            if (taskDefinition == null) {
                throw new RuntimeException("Task definition not found");
            }
        }

        return taskService.createTask(task);
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
