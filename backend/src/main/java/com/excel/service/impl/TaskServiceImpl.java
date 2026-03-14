package com.excel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excel.dto.TaskPageResponse;
import com.excel.dto.TaskQueryRequest;
import com.excel.entity.Task;
import com.excel.mapper.TaskMapper;
import com.excel.service.TaskService;
import org.apache.commons.lang3.StringUtils;
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

    @Override
    public TaskPageResponse queryTasks(TaskQueryRequest request) {
        int pageNo = request.getPageNo() == null || request.getPageNo() < 1 ? 1 : request.getPageNo();
        int pageSize = request.getPageSize() == null || request.getPageSize() < 1 ? 10 : request.getPageSize();

        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(request.getStatus())) {
            wrapper.eq(Task::getStatus, request.getStatus());
        }
        if (StringUtils.isNotBlank(request.getType())) {
            wrapper.eq(Task::getType, request.getType());
        }
        if (StringUtils.isNotBlank(request.getKeyword())) {
            String keyword = request.getKeyword().trim();
            wrapper.and(q -> q.like(Task::getName, keyword).or().like(Task::getDescription, keyword));
        }
        wrapper.orderByDesc(Task::getCreateTime);

        Page<Task> page = new Page<>(pageNo, pageSize);
        Page<Task> resultPage = baseMapper.selectPage(page, wrapper);

        TaskPageResponse response = new TaskPageResponse();
        response.setRecords(resultPage.getRecords());
        response.setTotal(resultPage.getTotal());
        response.setPageNo(resultPage.getCurrent());
        response.setPageSize(resultPage.getSize());
        response.setTotalPages(resultPage.getPages());
        return response;
    }

    @Override
    public List<Task> getRecentTasks(int limit) {
        int normalizedLimit = limit <= 0 ? 5 : limit;
        Page<Task> page = new Page<>(1, normalizedLimit);
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Task::getCreateTime);
        Page<Task> result = baseMapper.selectPage(page, wrapper);
        return result.getRecords();
    }
}
