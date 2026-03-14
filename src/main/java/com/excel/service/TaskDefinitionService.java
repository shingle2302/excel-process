package com.excel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.excel.entity.TaskDefinition;

import java.util.List;

public interface TaskDefinitionService extends IService<TaskDefinition> {
    TaskDefinition getByName(String name);
    List<TaskDefinition> getByClientId(Long clientId);
}