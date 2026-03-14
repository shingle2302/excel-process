package com.excel.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String apiKey;
    private String clientName;
    private long expiresIn;
}
