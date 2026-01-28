package com.pokestore.mcp.server.dto;

public record McpResponse(
        String content,
        String sessionId,
        boolean complete
) {}
