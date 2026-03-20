package com.excel.plugin;

import com.excel.entity.Task;

public interface TaskProcessorPlugin {
    String pluginName();
    boolean supports(String taskType);
    void process(Task task);
}
