package com.pokestore.orchestrateur.config;

import io.modelcontextprotocol.client.transport.customizer.McpSyncHttpClientRequestCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * Configuration OAuth2 pour l'authentification des appels MCP sortants.
 * <p>
 * L'orchestrateur doit s'authentifier auprès du MCP Server (qui est protégé par
 * OAuth2 resource server). Cette configuration utilise le flux
 * {@code client_credentials} : l'orchestrateur obtient un token au nom de
 * l'application (pas au nom de l'utilisateur) avec le client {@code mcp-auth}
 * enregistré dans Keycloak.
 * </p>
 * <p>
 * Le token est automatiquement mis en cache et renouvelé par Spring Security
 * avant expiration.
 * </p>
 */
@Configuration
public class McpAuthConfig {

    /** Identifiant de l'enregistrement OAuth2 client vers le MCP Server (défini dans application.yml). */
    @Value("${pokestore.mcp.oauth2-registration-id:mcp-server}")
    private String mcpRegistrationId;

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);

        manager.setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build());

        return manager;
    }

    /**
     * Customizes each outgoing MCP HTTP request by adding an OAuth2 Bearer token.
     * The token is obtained via client_credentials grant and automatically
     * cached/renewed by the {@link OAuth2AuthorizedClientManager} when it expires.
     */
    @Bean
    McpSyncHttpClientRequestCustomizer mcpAuthRequestCustomizer(
            OAuth2AuthorizedClientManager authorizedClientManager) {

        return (requestBuilder, method, uri, body, context) -> {
            OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                    .withClientRegistrationId(mcpRegistrationId)
                    .principal("orchestrateur")
                    .build();

            OAuth2AuthorizedClient authorizedClient =
                    authorizedClientManager.authorize(authorizeRequest);

            if (authorizedClient != null) {
                String tokenValue = authorizedClient.getAccessToken().getTokenValue();
                requestBuilder.header("Authorization", "Bearer " + tokenValue);
            }
        };
    }
}
