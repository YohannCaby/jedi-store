package com.pokestore.orchestrateur.controller;

import com.pokestore.orchestrateur.generated.api.ChatApi;
import com.pokestore.orchestrateur.generated.model.ChatRequest;
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

@RestController
@RequestMapping("/chat")
@Tag(name = "Chat", description = "API de chat avec l'assistant IA Pokemon")
public class ChatController implements ChatApi {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Envoie un message au chat", description = "Envoie un message et recoit la reponse en streaming (Server-Sent Events)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stream de reponse demarre"),
            @ApiResponse(responseCode = "400", description = "Requete invalide")
    })
    public Flux<String> chat(@Valid @RequestBody ChatRequest request) {
        return chatService.chat(request);
    }

    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "Supprime une session de chat", description = "Efface l'historique de conversation d'une session")
    @ApiResponse(responseCode = "204", description = "Session supprimee avec succes")
    public ResponseEntity<Void> clearSession(
            @Parameter(description = "ID de la session a supprimer") @PathVariable String sessionId) {
        chatService.clearSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
