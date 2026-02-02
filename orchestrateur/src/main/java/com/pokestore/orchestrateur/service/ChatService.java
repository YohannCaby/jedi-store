package com.pokestore.orchestrateur.service;

import com.pokestore.orchestrateur.generated.model.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ToolCallbackProvider toolCallbackProvider;
    private volatile ToolCallback[] cachedToolCallbacks;

    public ChatService(ChatClient.Builder chatClientBuilder,
                       ChatMemory chatMemory,
                       ToolCallbackProvider toolCallbackProvider) {
        this.chatMemory = chatMemory;
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        Tu es un assistant de support technique à une plateforme de e-commerce. Tu es spécialisé dans
                        l'aide au professionel sollicité à une demande d'aide client. Pour cela tu utilise les outils
                        mis à ta disposition.
                        Si la demande utilisateur n'est pas dans ton périmètre d'action rappel l'objet de ta fonction par 
                        la réponse suivante : "Je suis un assistant technique d'un plateforme de e-commerce, cette question
                        n'est pas dansmes compétences".
                        N'invente pas de réponses, ou ne simule pas de réponses.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public Flux<String> chat(ChatRequest request) {
        String conversationId = request.getSessionId() != null ? request.getSessionId() : "default";

        return Mono.fromCallable(this::getToolCallbacks)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(callbacks -> chatClient.prompt()
                        .user(request.getMessage())
                        .toolCallbacks(callbacks)
                        .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                        .stream()
                        .content());
    }

    private ToolCallback[] getToolCallbacks() {
        if (cachedToolCallbacks == null) {
            synchronized (this) {
                if (cachedToolCallbacks == null) {
                    cachedToolCallbacks = toolCallbackProvider.getToolCallbacks();
                }
            }
        }
        return cachedToolCallbacks;
    }

    public Mono<Void> clearSession(String sessionId) {
        return Mono.fromRunnable(() -> chatMemory.clear(sessionId));
    }
}
