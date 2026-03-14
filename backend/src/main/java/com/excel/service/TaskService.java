package com.excel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.excel.dto.TaskPageResponse;
import com.excel.dto.TaskQueryRequest;
import com.excel.entity.Task;

import java.util.List;

public interface TaskService extends IService<Task> {
    List<Task> getByStatus(String status);
    List<Task> getByClientId(Long clientId);
    Task createTask(Task task);
    void updateTaskStatus(Long taskId, String status);
    void updateTaskProgress(Long taskId, Integer progress);
    TaskPageResponse queryTasks(TaskQueryRequest request);
    List<Task> getRecentTasks(int limit);
}
