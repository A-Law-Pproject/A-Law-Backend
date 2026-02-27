package com.service.alaw.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = "bearerAuth";

        List<Server> servers = "prod".equals(activeProfile)
                ? List.of(
                    new Server().url("https://api.a-law.site").description("Production Server"),
                    new Server().url("http://localhost:8080").description("Local Server"))
                : List.of(
                    new Server().url("http://localhost:8080").description("Local Server"),
                    new Server().url("https://api.a-law.site").description("Production Server"));

        return new OpenAPI()
                .info(apiInfo())
                .servers(servers)
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

    private Info apiInfo() {
        return new Info()
                .title("A-Law API")
                .description("A-Law 서비스 API 문서")
                .version("v1.0.0")
                .contact(new Contact()
                        .name("A-Law Team")
                        .email("support@a-law.site")
                );
    }
}
