package com.allra.assignment.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
            .group("api")
            .pathsToMatch("/**")
            .addOperationCustomizer(operationCustomizer())
            .build();
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .version("v1.0.0")
            .title("allra-backend_assignment")
            .description("올라핀테크 백엔드 과제전형");

        return new OpenAPI()
            .info(info);
    }

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            operation
                .getTags()
                .sort(Comparator.naturalOrder());

            return operation;
        };
    }

}

