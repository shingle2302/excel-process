package com.excel.dto;

import com.excel.entity.Task;
import lombok.Data;

import java.util.List;

@Data
public class TaskPageResponse {
    private List<Task> records;
    private long total;
    private long totalPages;
    private long pageNo;
    private long pageSize;
}
