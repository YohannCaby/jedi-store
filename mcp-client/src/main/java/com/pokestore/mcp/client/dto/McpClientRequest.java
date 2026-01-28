package com.pokestore.mcp.client.dto;

public record McpClientRequest(
        String message,
        String sessionId
) {}
