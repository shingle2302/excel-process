package com.excel.controller;

import com.excel.annotation.RequirePermission;
import com.excel.service.TaskAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "数据分析与可视化")
public class AnalyticsController {

    private final TaskAnalyticsService taskAnalyticsService;

    @GetMapping("/status-distribution")
    @RequirePermission("analytics:read")
    @Operation(summary = "任务状态分布")
    public Map<String, Long> statusDistribution() {
        return taskAnalyticsService.statusDistribution();
    }

    @GetMapping("/task-trend")
    @RequirePermission("analytics:read")
    @Operation(summary = "任务趋势")
    public Map<String, Long> taskTrend(@RequestParam(defaultValue = "7") int days) {
        return taskAnalyticsService.recentTaskTrend(days);
    }

    @GetMapping("/report-summary")
    @RequirePermission("analytics:read")
    @Operation(summary = "汇总报表")
    public Map<String, Object> reportSummary() {
        return taskAnalyticsService.summaryReport();
    }
}
