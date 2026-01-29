package com.pokestore.orchestrateur.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orchestrateurOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PokeStore Orchestrateur API")
                        .description("API d'orchestration de chat avec Spring AI pour la boutique Pokemon")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Groupe Tech")
                                .email("contact@pokestore.com")))
                .servers(List.of(
                        new Server().url("/").description("Serveur par defaut")));
    }
}
