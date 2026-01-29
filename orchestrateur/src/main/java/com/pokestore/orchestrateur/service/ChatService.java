package com.pokestore.orchestrateur.service;

import com.pokestore.orchestrateur.dto.ChatRequest;
import com.pokestore.orchestrateur.dto.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ToolCallbackProvider toolCallbackProvider;

    public ChatService(ChatClient.Builder chatClientBuilder,
                       ToolCallbackProvider toolCallbackProvider) {
        this.chatClient = chatClientBuilder.build();
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public Mono<ChatResponse> chat(ChatRequest request) {
        return Mono.fromCallable(() -> {
            String response = chatClient.prompt()
                    .user(request.message())
                    .toolCallbacks(toolCallbackProvider.getToolCallbacks())
                    .call()
                    .content();
            return new ChatResponse(request.sessionId(), request.message(), response);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<String> chatStream(ChatRequest request) {
        return chatClient.prompt()
                .user(request.message())
                .toolCallbacks(toolCallbackProvider.getToolCallbacks())
                .stream()
                .content();
    }

    public Mono<Void> clearSession(String sessionId) {
        return Mono.empty();
    }
}
