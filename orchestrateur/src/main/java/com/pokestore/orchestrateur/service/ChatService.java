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
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Service principal de l'orchestrateur : gère la conversation entre l'utilisateur et le LLM.
 * <p>
 * Responsabilités :
 * <ul>
 *   <li>Construction du {@link ChatClient} avec le prompt système et la mémoire de conversation.</li>
 *   <li>Filtrage des outils MCP disponibles selon le rôle Keycloak de l'utilisateur
 *       (extrait du JWT via {@link #resolveUserLevel}).</li>
 *   <li>Streaming de la réponse du LLM via Server-Sent Events (SSE).</li>
 *   <li>Gestion du cycle de vie des sessions (effacement de la mémoire).</li>
 * </ul>
 * </p>
 *
 * <p>Flux de traitement pour un appel {@code /chat} :</p>
 * <pre>
 *   Requête HTTP → ChatController → ChatService#chat
 *     → résolution du niveau d'accès depuis le JWT
 *     → filtrage des outils MCP par niveau
 *     → appel ChatClient (Ollama via Spring AI)
 *     → LLM appelle les outils MCP filtrés si besoin
 *     → streaming SSE de la réponse
 * </pre>
 */
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
                        Tu es un agent d'assistance e-commerce.

                        RÈGLES STRICTES :
                        - Réponds UNIQUEMENT avec les informations fournies par le client ou retournées par un outil.
                        - Si tu n'as pas l'information → dis "Je n'ai pas cette information."
                        - Si aucun outil ne peut répondre → dis "Je ne peux pas traiter cette demande."
                        - N'INVENTE JAMAIS de données (numéro, date, statut, prix, outil).
                        - Si un humain est nécessaire → dis-le et arrête-toi.

                        FORMAT :
                        - Réponses courtes (2-3 phrases max).
                        - Pose UNE question si une info manque.
                        - Langue = langue du client.

                        INTERDIT :
                        - Inventer des outils ou fonctions.
                        - Simuler des résultats d'outils.
                        - Divulguer mots de passe ou données bancaires.
                        """)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public Flux<String> chat(ChatRequest request) {
        String conversationId = request.getSessionId() != null ? request.getSessionId() : "default";
        Authentication auth = getAuthentication();
        ToolCallback[] callbacks = toolCallbackProvider.getToolCallbacks();
        return streamResponse(request, conversationId, auth, callbacks);
    }

    /**
     * Filtre les outils MCP disponibles selon le niveau d'accès de l'utilisateur.
     * <p>
     * Convention : le nom d'un outil commence par son niveau requis (ex. {@code USER_getCustomerOrders}).
     * Un outil est accessible si {@code userLevel >= toolLevel}.
     * Les outils avec un nom invalide (sans préfixe reconnu) sont exclus avec un warning.
     * </p>
     *
     * @param callbacks liste complète des outils MCP enregistrés
     * @param auth      authentification Spring Security de l'utilisateur courant
     * @return tableau des outils accessibles pour cet utilisateur
     */
    private ToolCallback[] filterByRoles(ToolCallback[] callbacks, Authentication auth) {
        ToolCallback[] filterdTools = Arrays.stream(callbacks)
                .filter(c -> {
                    logger.debug("Tested tools : {}",c.getToolDefinition().name());
                    // Extraction du préfixe de niveau (ex. "USER" depuis "USER_getCustomerOrders")
                    String LevelAccessName = c.getToolDefinition().name().split("_")[0];
                    if(c.getToolDefinition().name().isEmpty() || !(AuthAccessLevel.isValid(LevelAccessName))){
                        logger.warn("Nom du Tool invalide : {}", c.getToolDefinition().name());
                        return false;
                    }
                    AuthAccessLevel authAccessLevel = resolveUserLevel(auth);
                    AuthAccessLevel toolAccessLevel = AuthAccessLevel.valueOf(LevelAccessName);
                    return authAccessLevel.compareTo(toolAccessLevel) >= 0;
                })
                .toArray(ToolCallback[]::new);
        Arrays.stream(filterdTools).forEach(tool -> logger.debug("Tools Filtered: {}",tool.getToolDefinition().name()));
        return filterdTools;
    }

    /**
     * Résout le niveau d'accès de l'utilisateur depuis son JWT Keycloak.
     * <p>
     * Extrait les rôles depuis le claim {@code realm_access.roles} du token.
     * Si plusieurs rôles correspondent à un {@link AuthAccessLevel}, le plus
     * élevé est retenu. Les utilisateurs sans JWT valide reçoivent le niveau {@code ALL}.
     * </p>
     *
     * @param auth authentification Spring Security (doit être un {@link JwtAuthenticationToken})
     * @return niveau d'accès résolu, {@link AuthAccessLevel#ALL} par défaut
     */
    private AuthAccessLevel resolveUserLevel(Authentication auth) {
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            return AuthAccessLevel.ALL;
        }
        Jwt jwt = jwtAuth.getToken();
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess == null) return AuthAccessLevel.ALL;

        Object roles = realmAccess.get("roles");
        if (roles instanceof List<?> roleList) {
            // On cherche le niveau le plus élevé parmi tous les rôles de l'utilisateur
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

    /**
     * Récupère l'authentification courante depuis le SecurityContext.
     * Retourne un token anonyme si aucune authentification n'est présente
     * (cas des appels sans JWT).
     *
     * @return l'authentification courante, jamais null
     */
    private Authentication getAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            return auth;
        }
        return new AnonymousAuthenticationToken("anonymous", "anonymous",
                AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
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

    public void clearSession(String sessionId) {
        chatMemory.clear(sessionId);
    }
}
