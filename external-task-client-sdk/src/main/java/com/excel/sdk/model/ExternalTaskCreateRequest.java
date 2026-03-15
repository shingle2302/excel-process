package com.excel.sdk.model;

public class ExternalTaskCreateRequest {
    public String clientId;
    public String clientSecret;
    public Long taskDefinitionId;
    public String name;
    public String description;
    public String type;
    public String dataFetchType;
    public Long dataSourceId;
    public String querySql;
    public String httpMethod;
    public String httpUrl;
    public Boolean httpNeedAuth;
    public String authUrl;
    public String authParams;
    public String requestParams;
    public String params;
}
