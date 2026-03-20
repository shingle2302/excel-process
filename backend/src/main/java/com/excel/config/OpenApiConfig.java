package com.excel.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Excel处理服务API")
                        .version("1.1")
                        .description("Excel处理服务的RESTful API文档"))
                .addSecurityItem(new SecurityRequirement().addList("apiKeyAuth"))
                .components(new Components()
                        .addSecuritySchemes("apiKeyAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .name("X-API-Key")
                                .in(SecurityScheme.In.HEADER)));
    }
}
