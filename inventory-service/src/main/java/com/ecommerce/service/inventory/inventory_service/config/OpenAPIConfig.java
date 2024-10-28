package com.ecommerce.service.inventory.inventory_service.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI InventoryServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Inventory Service API")
                        .description("This is a REST API for Inventory Service")
                        .version("v0.0.1")
                        .license(new License().name("Apache 2.0")))
                .components(new io.swagger.v3.oas.models.Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER).name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .externalDocs(new ExternalDocumentation()
                        .description("You can refer to the Inventory Service Wiki Documentation")
                        .url("https://inventory-service-dummy-url/docs"));
    }

}
