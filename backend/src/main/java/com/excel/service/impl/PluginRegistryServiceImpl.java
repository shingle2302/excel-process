package com.excel.service.impl;

import com.excel.exception.BusinessException;
import com.excel.plugin.TaskProcessorPlugin;
import com.excel.service.PluginRegistryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PluginRegistryServiceImpl implements PluginRegistryService {

    private final List<TaskProcessorPlugin> plugins;

    public PluginRegistryServiceImpl(List<TaskProcessorPlugin> plugins) {
        this.plugins = plugins;
    }

    @Override
    public List<String> listPluginNames() {
        return plugins.stream().map(TaskProcessorPlugin::pluginName).toList();
    }

    @Override
    public TaskProcessorPlugin resolve(String taskType) {
        return plugins.stream().filter(p -> p.supports(taskType)).findFirst()
                .orElseThrow(() -> new BusinessException(404, "未找到可用任务处理插件"));
    }
}
