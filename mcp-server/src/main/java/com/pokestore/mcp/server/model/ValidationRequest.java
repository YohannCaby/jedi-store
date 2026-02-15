package com.pokestore.mcp.server.model;

public class ValidationRequest {
    boolean validated;

    public ValidationRequest() {
    }

    public ValidationRequest(boolean validated) {
        this.validated = validated;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}
