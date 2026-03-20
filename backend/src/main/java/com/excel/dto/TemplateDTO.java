package com.excel.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TemplateDTO {
    private String id;
    private String name;
    private String type;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
