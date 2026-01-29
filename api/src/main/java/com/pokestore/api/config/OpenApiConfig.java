package com.pokestore.api.config;

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
    public OpenAPI pokeStoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PokeStore API")
                        .description("API REST pour la gestion de la boutique Pokemon")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Groupe Tech")
                                .email("contact@pokestore.com")))
                .servers(List.of(
                        new Server().url("/").description("Serveur par defaut")));
    }
}
