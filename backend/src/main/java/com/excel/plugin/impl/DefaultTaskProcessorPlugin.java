package com.excel.plugin.impl;

import com.excel.entity.Task;
import com.excel.plugin.TaskProcessorPlugin;
import org.springframework.stereotype.Component;

@Component
public class DefaultTaskProcessorPlugin implements TaskProcessorPlugin {
    @Override
    public String pluginName() {
        return "default-task-processor";
    }

    @Override
    public boolean supports(String taskType) {
        return true;
    }

    @Override
    public void process(Task task) {
        // 默认插件占位实现，预留给后续导入导出策略扩展
    }
}
