package com.pokestore.orchestrateur.dto;

public record ChatResponse(
        String sessionId,
        String message,
        String response
) {}
