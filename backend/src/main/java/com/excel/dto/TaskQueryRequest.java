package com.excel.dto;

import lombok.Data;

@Data
public class TaskQueryRequest {
    private String keyword;
    private String status;
    private String type;
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}
