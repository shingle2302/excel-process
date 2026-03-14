package com.excel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excel.entity.TaskDefinition;
import com.excel.mapper.TaskDefinitionMapper;
import com.excel.service.TaskDefinitionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskDefinitionServiceImpl extends ServiceImpl<TaskDefinitionMapper, TaskDefinition> implements TaskDefinitionService {

    @Override
    public TaskDefinition getByName(String name) {
        LambdaQueryWrapper<TaskDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskDefinition::getName, name);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<TaskDefinition> getByClientId(Long clientId) {
        LambdaQueryWrapper<TaskDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskDefinition::getClientId, clientId);
        return baseMapper.selectList(wrapper);
    }
}