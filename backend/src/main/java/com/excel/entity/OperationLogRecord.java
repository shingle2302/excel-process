package com.excel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLogRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long clientId;

    private String operationType;

    private String operationDetail;

    private LocalDateTime createTime;
}
