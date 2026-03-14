package com.excel.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String clientId;
    private String clientSecret;
}
