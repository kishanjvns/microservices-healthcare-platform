package com.tech.kj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerDocConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("member Management API")
                        .version("v1.0.0")
                        .description("Services for managing member in the Health Care System")
                        .contact(new Contact()
                                .name("API Support Team")
                                .email("api-support@yourcompany.com")));
    }
}
