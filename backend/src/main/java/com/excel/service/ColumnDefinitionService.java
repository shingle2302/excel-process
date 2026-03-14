package com.excel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.excel.entity.ColumnDefinition;

import java.util.List;

public interface ColumnDefinitionService extends IService<ColumnDefinition> {
    List<ColumnDefinition> getByTaskDefinitionId(Long taskDefinitionId);
    void deleteByTaskDefinitionId(Long taskDefinitionId);
}