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
@TableName("column_definition")
public class ColumnDefinition implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long taskDefinitionId;

    private String fieldName;

    private String columnName;

    private String columnType;

    private String columnFormat;

    private String description;

    private String validationRules;

    private String defaultValue;

    private String mappingRules;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}