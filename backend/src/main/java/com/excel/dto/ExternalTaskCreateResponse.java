package com.excel.dto;

import com.excel.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExternalTaskCreateResponse {
    private String clientId;
    private String clientName;
    private Task task;
}
