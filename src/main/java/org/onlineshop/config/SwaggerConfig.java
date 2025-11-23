package org.onlineshop.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(getServers())
                .info(getApiInfo())
                .components(getComponents())
                .security(getSecurityRequirements());
    }

    private List<Server> getServers() {
        return List.of(
                new Server()
                        .url("https://api.onlineshop.name/")
                        .description("Production API Server"),
                new Server()
                        .url("http://localhost:8080")
                        .description("Local Development Server"),
                new Server()
                        .url("https://onlineshop050525-85ba69944bb4.herokuapp.com/")
                        .description("Staging/Heroku Server")
        );
    }

    private Info getApiInfo() {
        return new Info()
                .title("Online Shop REST API")
                .description("""
                        Comprehensive REST API for Online Shop application.
                        
                        ## 🔐 Authentication
                        - JWT-based authentication
                        - Role-based authorization (USER, ADMIN)
                        
                        ## 📋 Main Features
                        - User registration and authentication
                        - Product catalog management
                        - Shopping cart operations
                        - Order processing
                        - Payment integration
                        - Email confirmation
                        
                        ## 🛠️ Technologies
                        - Spring Boot 3.x
                        - Spring Security
                        - JWT Authentication
                        - PostgreSQL
                        - Spring Data JPA
                        
                        ### 📚 API Version: v1.0
                        """)
                .version("v1.0")
                .contact(new Contact()
                        .name("Development Team - Group 050525-m-be")
                        .url("https://github.com/YuriyDolgikh/onlineshop"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"))
                .extensions(Map.of(
                        "x-api-version", "1.0",
                        "x-supported-languages", List.of("en", "ru"),
                        "x-rate-limit", "1000 requests per hour"
                ));
    }

    private Components getComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT Authorization header using the Bearer scheme. Example: \\\"Authorization: Bearer {token}\\\""))
                .addSecuritySchemes("basicAuth", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        .description("Basic authentication for admin endpoints"))
                .addResponses("BadRequest", new ApiResponse()
                        .description("Bad Request - Invalid input parameters")
                        .content(new Content().addMediaType("application/json",
                                new MediaType().example("{\\\"error\\\": \\\"Invalid input\\\", \\\"code\\\": 400}"))))
                .addResponses("Unauthorized", new ApiResponse()
                        .description("Unauthorized - Invalid or missing authentication")
                        .content(new Content().addMediaType("application/json",
                                new MediaType().example("{\\\"error\\\": \\\"Unauthorized\\\", \\\"code\\\": 401}"))))
                .addResponses("NotFound", new ApiResponse()
                        .description("Resource Not Found")
                        .content(new Content().addMediaType("application/json",
                                new MediaType().example("{\\\"error\\\": \\\"Resource not found\\\", \\\"code\\\": 404}"))))
                .addResponses("InternalError", new ApiResponse()
                        .description("Internal Server Error")
                        .content(new Content().addMediaType("application/json",
                                new MediaType().example("{\\\"error\\\": \\\"Internal server error\\\", \\\"code\\\": 500}"))));
    }

    private List<SecurityRequirement> getSecurityRequirements() {
        return List.of(
                new SecurityRequirement().addList("bearerAuth")
        );
    }
}