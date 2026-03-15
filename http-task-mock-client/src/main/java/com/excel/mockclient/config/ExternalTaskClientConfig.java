package com.excel.mockclient.config;

import com.excel.sdk.ExternalTaskClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalTaskClientConfig {

    @Bean
    public ExternalTaskClient externalTaskClient(@Value("${target.server-base-url:http://localhost:8080}") String serverBaseUrl) {
        return new ExternalTaskClient(serverBaseUrl);
    }
}
