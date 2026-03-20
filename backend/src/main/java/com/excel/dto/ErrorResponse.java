package com.excel.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private int code;
    private String message;
    private String details;
    private String path;
    private LocalDateTime timestamp;
}
