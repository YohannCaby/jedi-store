package com.pokestore.orchestrateur.controller;

import com.pokestore.orchestrateur.dto.ChatRequest;
import com.pokestore.orchestrateur.dto.ChatResponse;
import com.pokestore.orchestrateur.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat", description = "API de chat avec l'assistant IA Pokemon")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(summary = "Envoie un message au chat", description = "Envoie un message et recoit une reponse complete de l'assistant IA")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reponse generee avec succes"),
            @ApiResponse(responseCode = "400", description = "Requete invalide")
    })
    public Mono<ResponseEntity<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Envoie un message en mode streaming", description = "Envoie un message et recoit la reponse en streaming (Server-Sent Events)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stream de reponse demarre"),
            @ApiResponse(responseCode = "400", description = "Requete invalide")
    })
    public Flux<String> chatStream(@Valid @RequestBody ChatRequest request) {
        return chatService.chatStream(request);
    }

    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "Supprime une session de chat", description = "Efface l'historique de conversation d'une session")
    @ApiResponse(responseCode = "204", description = "Session supprimee avec succes")
    public Mono<ResponseEntity<Void>> clearSession(
            @Parameter(description = "ID de la session a supprimer") @PathVariable String sessionId) {
        return chatService.clearSession(sessionId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}
