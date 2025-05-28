package com.clinic.clinic_personnel_system.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Clinic Personnel System API", version = "v1"))
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi clinicApi() {
        return GroupedOpenApi.builder()
                .group("clinic-api")
                .pathsToMatch("/api/**")
                .build();
    }
}