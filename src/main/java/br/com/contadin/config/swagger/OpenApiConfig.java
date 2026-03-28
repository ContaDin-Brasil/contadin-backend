package br.com.contadin.config.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API RESTful ContaDin",
                description = "Documentação da API do projeto ContaDin",
                version = "1.0.0",
                contact = @Contact(
                        name = "ContaDin",
                        url = "https://github.com/ContaDin-Brasil/contadin-backend",
                        email = "contadinbrasil01@gmail.com"
                ),
                license = @License(name = "Proprietary - All rights reserved")
        )
)
@SecurityScheme(
        name = "Bearer", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"
)

public class OpenApiConfig {
}