package com.pokestore.mcp.client.dto;

public record McpClientResponse(
        String content,
        String sessionId,
        boolean complete
) {}
