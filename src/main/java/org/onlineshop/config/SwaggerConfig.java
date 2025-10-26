package org.onlineshop.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server")
                ))
                .info(new Info()
                        .title("Online Shop API")
                        .description("API for online shop application")
                        .version("v1.0")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Group 050525-m-be")
                                .url("https://github.com/YuriyDolgikh/onlineshop")));
    }
}