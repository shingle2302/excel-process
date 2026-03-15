package com.excel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataSourceConnectionTestResponse {
    private Boolean success;
    private String message;
}
