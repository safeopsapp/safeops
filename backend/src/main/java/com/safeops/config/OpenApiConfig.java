package com.safeops.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "SafeOps API",
        description = "SafeOps Construction Safety Management Platform REST API",
        version = "v1",
        contact = @Contact(name = "SafeOps Team")
    ),
    servers = {
        @Server(url = "/", description = "Default Server")
    }
)
@SecurityScheme(
    name = "BearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER,
    description = "Firebase ID Token. Obtain from Firebase Authentication and pass as Bearer token."
)
public class OpenApiConfig {
    // Configuration is annotation-driven
}
