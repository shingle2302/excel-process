package com.excel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("task")
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long taskDefinitionId;

    private String name;

    private String description;

    private String type;

    private String status;

    private String filePath;

    private String dataFetchType;

    private Long dataSourceId;

    private String querySql;

    private String httpMethod;

    private String httpUrl;

    private Boolean httpNeedAuth;

    private String authUrl;

    private String authParams;

    private String requestParams;

    private String params;

    private String result;

    private String errorMessage;

    private Integer progress;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}