package com.jedistore.mcp.server.model;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public class ValidationRequest {
    @JsonPropertyDescription("Did you falidate this action?")
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
