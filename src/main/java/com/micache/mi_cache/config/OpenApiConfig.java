package com.micache.mi_cache.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Configuramos la información básica de la API
        Contact contact = new Contact();
        contact.setEmail("dev@tuempresa.com");
        contact.setName("Equipo de Backend");
        contact.setUrl("https://tuempresa.com");

        Info info = new Info()
                .title("API de Gestión - Observabilidad Ready")
                .version("1.0.0")
                .contact(contact)
                .description("Documentación de la API siguiendo buenas prácticas y sin dependencias innecesarias.");

        return new OpenAPI().info(info);
    }
}
