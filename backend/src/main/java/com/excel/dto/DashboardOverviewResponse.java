package com.excel.dto;

import com.excel.entity.Task;
import lombok.Data;

import java.util.List;

@Data
public class DashboardOverviewResponse {
    private long taskCount;
    private long taskDefinitionCount;
    private long clientCount;
    private long columnDefinitionCount;

    private long completedCount;
    private long runningCount;
    private long pendingCount;
    private long failedCount;
    private long pausedCount;

    private int completionRate;
    private List<Task> recentTasks;
}
