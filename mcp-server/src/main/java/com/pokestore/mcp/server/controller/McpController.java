package com.pokestore.mcp.server.controller;

import com.pokestore.mcp.server.dto.McpRequest;
import com.pokestore.mcp.server.dto.McpResponse;
import com.pokestore.mcp.server.service.OllamaService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final OllamaService ollamaService;

    public McpController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping
    public ResponseEntity<McpResponse> chat(@RequestBody McpRequest request) {
        String response = ollamaService.chat(request.message(), request.sessionId());
        return ResponseEntity.ok(new McpResponse(response, request.sessionId(), true));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<McpResponse> chatStream(@RequestBody McpRequest request) {
        return ollamaService.chatStream(request.message(), request.sessionId())
                .map(content -> new McpResponse(content, request.sessionId(), false))
                .concatWith(Flux.just(new McpResponse("", request.sessionId(), true)));
    }

    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> clearSession(@PathVariable String sessionId) {
        ollamaService.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
