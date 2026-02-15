package com.pokestore.orchestrateur.service;

import com.pokestore.orchestrateur.generated.model.ChatRequest;
import com.pokestore.shared.AuthAccessLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final static Logger logger = LoggerFactory.getLogger(ChatService.class);
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final ToolCallbackProvider toolCallbackProvider;

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
                        
                        Données : Tu as accès à un ensemble de connecteur MCP (Outils) permettant de récolter des informations. Tu dois te baser uniquement sur les informations qui te sont fournit par l'utiliseteur ou les outils à ta disposition. Si aucun outils ne permet de répondre à la demande, réponds que tu ne peux pas répondre à la demande.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public Flux<String> chat(ChatRequest request) {
        String conversationId = request.getSessionId() != null ? request.getSessionId() : "default";
        return Mono.zip(getAuthentication(), getToolCallbacks())
                .flatMapMany(tuple -> streamResponse(request, conversationId, tuple.getT1(), tuple.getT2()));

    }

    private ToolCallback[] filterByRoles(ToolCallback[] callbacks, Authentication auth) {
        ToolCallback[] filterdTools = Arrays.stream(callbacks)
                .filter(c -> {
                    logger.debug("Tested tools : {}",c.getToolDefinition().name());
                    c.getToolDefinition().name();
                    if(c.getToolDefinition().name().isEmpty() || !(AuthAccessLevel.isValid(c.getToolDefinition().name().split("_")[0]))){
                        return false;
                    }
                    AuthAccessLevel authAccessLevel = resolveUserLevel(auth);
                    AuthAccessLevel toolAccessLevel = AuthAccessLevel.valueOf(c.getToolDefinition().name().split("_")[0]);
                    return authAccessLevel.compareTo(toolAccessLevel) >= 0;
                })
                .toArray(ToolCallback[]::new);
        Arrays.stream(filterdTools).forEach(tool -> logger.debug("Tools Filtered: {}",tool.getToolDefinition().name()));
        return filterdTools;
    }
    private AuthAccessLevel resolveUserLevel(Authentication auth) {
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            return AuthAccessLevel.ALL;
        }
        Jwt jwt = jwtAuth.getToken();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) return AuthAccessLevel.ALL;

        Object roles = realmAccess.get("roles");
        if (roles instanceof List<?> roleList) {
            return roleList.stream()
                    .map(Object::toString)
                    .map(String::toUpperCase)
                    .filter(AuthAccessLevel::isValid)
                    .map(AuthAccessLevel::valueOf)
                    .sorted()
                    .findFirst()
                    .orElse(AuthAccessLevel.ALL);
        }
        return AuthAccessLevel.ALL;
    }
    private Mono<Authentication> getAuthentication() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication);
    }

    private Mono<ToolCallback[]> getToolCallbacks() {
        return Mono.fromCallable(toolCallbackProvider::getToolCallbacks)
                .subscribeOn(Schedulers.boundedElastic());
    }
    private Flux<String> streamResponse(ChatRequest request, String conversationId,
                                        Authentication auth, ToolCallback[] callbacks) {
        return chatClient.prompt()
                .user(request.getMessage())
                .toolCallbacks(filterByRoles(callbacks, auth))
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }

    public Mono<Void> clearSession(String sessionId) {
        return Mono.fromRunnable(() -> chatMemory.clear(sessionId));
    }
}
