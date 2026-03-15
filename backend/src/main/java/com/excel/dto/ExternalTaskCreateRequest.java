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
    private String dataFetchType;
    private Long dataSourceId;
    private String querySql;
    private String httpMethod;
    private String httpUrl;
    private Boolean httpNeedAuth;
    private String authUrl;
    private String authParams;
    private String requestParams;
    private String params;
}
