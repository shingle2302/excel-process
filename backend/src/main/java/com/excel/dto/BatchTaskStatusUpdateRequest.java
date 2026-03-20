package com.excel.dto;

import lombok.Data;

import java.util.List;

@Data
public class BatchTaskStatusUpdateRequest {
    private List<Long> taskIds;
    private String status;
}
