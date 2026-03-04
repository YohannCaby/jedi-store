package com.pokestore.orchestrateur.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration explicite de la chaîne de filtres Spring Security.
 * <p>
 * Sans ce bean, l'ajout de la dépendance oauth2-client (utilisée pour le
 * client_credentials vers le MCP server) déclenche l'auto-configuration
 * par défaut de Spring Security, qui active notamment la protection CSRF.
 * Toute requête POST (ex. /chat) est alors rejetée avec un 403
 * « Invalid CSRF token ».
 * <p>
 * Pour une API REST protégée par Bearer token JWT, la protection CSRF
 * n'est pas nécessaire : le navigateur n'envoie pas automatiquement le
 * token dans un cookie, ce qui rend les attaques CSRF inopérantes.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactivation du CSRF : inutile pour une API REST authentifiée par Bearer token
                .csrf(csrf -> csrf.disable())
                // Toutes les requêtes nécessitent une authentification
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                // Validation des JWT via l'issuer-uri configuré dans application.yml
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                );
        return http.build();
    }
}
