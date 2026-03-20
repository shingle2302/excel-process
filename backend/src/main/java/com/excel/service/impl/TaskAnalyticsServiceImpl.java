package com.excel.service.impl;

import com.excel.entity.Task;
import com.excel.service.TaskAnalyticsService;
import com.excel.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskAnalyticsServiceImpl implements TaskAnalyticsService {

    private final TaskService taskService;

    @Override
    public Map<String, Long> statusDistribution() {
        return taskService.list().stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));
    }

    @Override
    public Map<String, Long> recentTaskTrend(int days) {
        int normalizedDays = days <= 0 ? 7 : days;
        List<Task> tasks = taskService.list();
        Map<String, Long> trend = new LinkedHashMap<>();
        for (int i = normalizedDays - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            long count = tasks.stream()
                    .filter(t -> t.getCreateTime() != null && date.equals(t.getCreateTime().toLocalDate()))
                    .count();
            trend.put(date.toString(), count);
        }
        return trend;
    }

    @Override
    public Map<String, Object> summaryReport() {
        List<Task> tasks = taskService.list();
        long total = tasks.size();
        long success = tasks.stream().filter(t -> "已完成".equals(t.getStatus())).count();
        long failed = tasks.stream().filter(t -> "失败".equals(t.getStatus())).count();
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("totalTasks", total);
        report.put("successTasks", success);
        report.put("failedTasks", failed);
        report.put("successRate", total == 0 ? 0D : (double) success / total);
        report.put("statusDistribution", statusDistribution());
        return report;
    }
}
