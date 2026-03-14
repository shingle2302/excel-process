package com.excel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excel.entity.Task;
import com.excel.mapper.TaskMapper;
import com.excel.service.TaskService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Override
    public List<Task> getByStatus(String status) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getStatus, status);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Task> getByClientId(Long clientId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getTaskDefinitionId, clientId);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Task createTask(Task task) {
        task.setStatus("待处理");
        task.setProgress(0);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        baseMapper.insert(task);
        return task;
    }

    @Override
    public void updateTaskStatus(Long taskId, String status) {
        Task task = new Task();
        task.setId(taskId);
        task.setStatus(status);
        task.setUpdateTime(LocalDateTime.now());
        if ("处理中".equals(status)) {
            task.setStartTime(LocalDateTime.now());
        } else if ("已完成".equals(status) || "失败".equals(status)) {
            task.setEndTime(LocalDateTime.now());
        }
        baseMapper.updateById(task);
    }

    @Override
    public void updateTaskProgress(Long taskId, Integer progress) {
        Task task = new Task();
        task.setId(taskId);
        task.setProgress(progress);
        task.setUpdateTime(LocalDateTime.now());
        baseMapper.updateById(task);
    }
}