package com.excel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excel.entity.ColumnDefinition;
import com.excel.mapper.ColumnDefinitionMapper;
import com.excel.service.ColumnDefinitionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColumnDefinitionServiceImpl extends ServiceImpl<ColumnDefinitionMapper, ColumnDefinition> implements ColumnDefinitionService {

    @Override
    public List<ColumnDefinition> getByTaskDefinitionId(Long taskDefinitionId) {
        LambdaQueryWrapper<ColumnDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ColumnDefinition::getTaskDefinitionId, taskDefinitionId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public void deleteByTaskDefinitionId(Long taskDefinitionId) {
        LambdaQueryWrapper<ColumnDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ColumnDefinition::getTaskDefinitionId, taskDefinitionId);
        baseMapper.delete(wrapper);
    }
}