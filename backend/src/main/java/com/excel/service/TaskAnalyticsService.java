package com.excel.service;

import java.util.Map;

public interface TaskAnalyticsService {
    Map<String, Long> statusDistribution();
    Map<String, Long> recentTaskTrend(int days);
    Map<String, Object> summaryReport();
}
