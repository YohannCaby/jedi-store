package com.pokestore.mcp.server.dto;

public record McpRequest(
        String message,
        String sessionId
) {}
