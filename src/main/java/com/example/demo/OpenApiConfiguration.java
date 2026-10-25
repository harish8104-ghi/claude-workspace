package com.example.demo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Spring Java 25 Demo API",
        version = "1.0.0",
        description = "OpenAPI documentation for the Spring Java 25 demo REST API"))
public class OpenApiConfiguration {
}
