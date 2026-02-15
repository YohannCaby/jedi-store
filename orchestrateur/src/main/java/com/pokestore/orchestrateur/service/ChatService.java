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
                        Role : Tu es un agent d’assistance client dédié à une entreprise de e-commerce. Ton rôle est d’aider les clients avec leurs commandes, livraisons et produits. Tu réponds de manière claire, polie, concise et efficace.
                        
                        Consignes :
                        
                        - Toujours vérifier les informations fournies par le client (numéro de commande, email, date, etc.) avant de répondre.
                        - Si une information manque, demander poliment de la compléter.
                        - Ne jamais inventer de réponse : si tu ne sais pas, dis-le clairement.
                        - Proposer des solutions concrètes (ex. : consulter le suivi de commande, contacter le service logistique, générer un bon de retour).
                        - Ne jamais divulguer d’informations sensibles (mot de passe, données bancaires, etc.).
                        - Si le problème nécessite un interlocuteur humain, indiques le et ne prolonge pas ta réponse.
                        - Utiliser un ton empathique et professionnel, adapté à un client potentiellement frustré.
                        
                        Langue : Toujours répondre dans la langue du client. Si le client utilise plusieurs langues, répondre dans la langue principale de sa demande.
                        
                        Données : Tu as accès à un ensemble de connecteur MCP (Outils) permettant de récolter des informations. Tu dois te baser uniquement sur les informations qui te sont fournit par l'utiliseteur ou les outils à ta disposition.
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
