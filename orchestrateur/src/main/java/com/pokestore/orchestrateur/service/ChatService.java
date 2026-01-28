package com.pokestore.orchestrateur.service;

import com.pokestore.mcp.client.McpClient;
import com.pokestore.mcp.client.dto.McpClientResponse;
import com.pokestore.orchestrateur.dto.ChatRequest;
import com.pokestore.orchestrateur.dto.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ChatService {

    private final McpClient mcpClient;

    public ChatService(McpClient mcpClient) {
        this.mcpClient = mcpClient;
    }

    public ChatResponse chat(ChatRequest request) {
        McpClientResponse mcpResponse = mcpClient
                .chat(request.message(), request.sessionId())
                .block();

        String responseContent = mcpResponse != null ? mcpResponse.content() : "Error: No response from MCP server";

        return new ChatResponse(
                request.sessionId(),
                request.message(),
                responseContent
        );
    }

    public Flux<String> chatStream(ChatRequest request) {
        return mcpClient.chatStream(request.message(), request.sessionId())
                .filter(response -> !response.complete())
                .map(McpClientResponse::content);
    }

    public void clearSession(String sessionId) {
        mcpClient.clearSession(sessionId).subscribe();
    }
}
