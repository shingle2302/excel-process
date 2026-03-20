package com.excel.service.impl;

import com.excel.exception.BusinessException;
import com.excel.service.AsyncTaskProcessingService;
import com.excel.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsyncTaskProcessingServiceImpl implements AsyncTaskProcessingService {

    private final TaskService taskService;

    @Override
    @Async("taskExecutor")
    public void processTaskAsync(Long taskId) {
        if (taskService.getById(taskId) == null) {
            throw new BusinessException(404, "任务不存在");
        }
        taskService.updateTaskStatus(taskId, "处理中");
        for (int progress = 10; progress <= 100; progress += 30) {
            taskService.updateTaskProgress(taskId, progress);
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                taskService.updateTaskStatus(taskId, "失败");
                return;
            }
        }
        taskService.updateTaskStatus(taskId, "已完成");
    }
}
