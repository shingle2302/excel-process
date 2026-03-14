package com.excel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.excel.entity.Task;

import java.util.List;

public interface TaskService extends IService<Task> {
    List<Task> getByStatus(String status);
    List<Task> getByClientId(Long clientId);
    Task createTask(Task task);
    void updateTaskStatus(Long taskId, String status);
    void updateTaskProgress(Long taskId, Integer progress);
}