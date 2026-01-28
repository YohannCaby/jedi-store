package com.pokestore.orchestrateur.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatRequest(
        @NotBlank(message = "Session ID is required")
        String sessionId,

        @NotBlank(message = "Message is required")
        String message
) {}
