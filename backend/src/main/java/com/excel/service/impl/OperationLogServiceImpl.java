package com.excel.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.excel.entity.OperationLogRecord;
import com.excel.mapper.OperationLogMapper;
import com.excel.service.OperationLogService;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLogRecord> implements OperationLogService {
}
