package com.pokestore.api.controller.dto;

public record CustomerDto(
        Long id,
        String name,
        String email,
        String address
) {}
