package com.pokestore.api.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateStatusRequest(
        @NotNull(message = "Status is required")
        @Pattern(regexp = "IN_PROGRESS|DELIVERED|CANCELLED", message = "Status must be IN_PROGRESS, DELIVERED, or CANCELLED")
        String status
) {}
