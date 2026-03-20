package com.excel.service;

import com.excel.plugin.TaskProcessorPlugin;

import java.util.List;

public interface PluginRegistryService {
    List<String> listPluginNames();
    TaskProcessorPlugin resolve(String taskType);
}
