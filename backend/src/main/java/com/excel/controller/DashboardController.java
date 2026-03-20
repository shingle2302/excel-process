package com.excel.controller;

import com.excel.annotation.RequirePermission;
import com.excel.dto.DashboardOverviewResponse;
import com.excel.entity.Task;
import com.excel.service.ClientService;
import com.excel.service.ColumnDefinitionService;
import com.excel.service.TaskDefinitionService;
import com.excel.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "看板")
public class DashboardController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskDefinitionService taskDefinitionService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ColumnDefinitionService columnDefinitionService;

    @GetMapping("/overview")
    @RequirePermission("dashboard:read")
    @Operation(summary = "获取首页概览")
    public DashboardOverviewResponse getOverview(@RequestParam(defaultValue = "6") Integer recentLimit) {
        List<Task> allTasks = taskService.list();

        long completedCount = allTasks.stream().filter(task -> "已完成".equals(task.getStatus())).count();
        long runningCount = allTasks.stream().filter(task -> "处理中".equals(task.getStatus())).count();
        long pendingCount = allTasks.stream().filter(task -> "待处理".equals(task.getStatus())).count();
        long failedCount = allTasks.stream().filter(task -> "失败".equals(task.getStatus())).count();
        long pausedCount = allTasks.stream().filter(task -> "暂停".equals(task.getStatus())).count();

        DashboardOverviewResponse response = new DashboardOverviewResponse();
        response.setTaskCount(allTasks.size());
        response.setTaskDefinitionCount(taskDefinitionService.count());
        response.setClientCount(clientService.count());
        response.setColumnDefinitionCount(columnDefinitionService.count());

        response.setCompletedCount(completedCount);
        response.setRunningCount(runningCount);
        response.setPendingCount(pendingCount);
        response.setFailedCount(failedCount);
        response.setPausedCount(pausedCount);

        int completionRate = allTasks.isEmpty() ? 0 : (int) Math.round((double) completedCount * 100 / allTasks.size());
        response.setCompletionRate(completionRate);
        response.setRecentTasks(taskService.getRecentTasks(recentLimit));
        return response;
    }
}
