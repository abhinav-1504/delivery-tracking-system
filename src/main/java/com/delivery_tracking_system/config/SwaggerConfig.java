package com.delivery_tracking_system.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Delivery Tracking System API")
                        .description("Backend API for delivery tracking with roles and JWT")
                        .version("1.0.0")
                );
    }
}
