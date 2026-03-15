package com.excel.dto;

import lombok.Data;

@Data
public class ExternalTaskCreateRequest {
    private String clientId;
    private String clientSecret;
    private Long taskDefinitionId;
    private String name;
    private String description;
    private String type;
    private String requestParams;
    private String params;
}
