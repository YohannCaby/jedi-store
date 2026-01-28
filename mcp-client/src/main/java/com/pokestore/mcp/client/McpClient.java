package com.pokestore.mcp.client;

import com.pokestore.mcp.client.dto.McpClientRequest;
import com.pokestore.mcp.client.dto.McpClientResponse;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class McpClient {

    private final WebClient webClient;

    public McpClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public McpClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<McpClientResponse> chat(String message, String sessionId) {
        McpClientRequest request = new McpClientRequest(message, sessionId);

        return webClient.post()
                .uri("/mcp")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(McpClientResponse.class);
    }

    public Flux<McpClientResponse> chatStream(String message, String sessionId) {
        McpClientRequest request = new McpClientRequest(message, sessionId);

        return webClient.post()
                .uri("/mcp/stream")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .bodyToFlux(McpClientResponse.class);
    }

    public Mono<Void> clearSession(String sessionId) {
        return webClient.delete()
                .uri("/mcp/session/{sessionId}", sessionId)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
