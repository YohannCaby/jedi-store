package com.pokestore.mcp.server.service;

import com.pokestore.mcp.server.tools.PokeStoreTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OllamaService {

    private final ChatClient chatClient;
    private final PokeStoreTools pokeStoreTools;
    private final Map<String, List<Message>> sessionHistory = new ConcurrentHashMap<>();

    private static final String SYSTEM_PROMPT = """
            You are a helpful assistant for the Poke Store, a Pokemon merchandise shop.
            You can help customers with:
            - Browsing products (plush toys, trading cards, apparel, collectibles, etc.)
            - Viewing their order history
            - Creating new orders
            - Updating order status
            - Searching for specific products

            Be friendly and helpful. Use the available tools to assist customers.
            When discussing prices, always mention they are in dollars.
            If a customer asks about Pokemon, you can provide general information, but focus on helping them shop.
            """;

    public OllamaService(OllamaChatModel chatModel, PokeStoreTools pokeStoreTools) {
        this.pokeStoreTools = pokeStoreTools;
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultTools(pokeStoreTools)
                .build();
    }

    public String chat(String message, String sessionId) {
        List<Message> history = sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());

        history.add(new UserMessage(message));

        String response = chatClient.prompt()
                .messages(history)
                .call()
                .content();

        history.add(new AssistantMessage(response));

        if (history.size() > 40) {
            history.subList(0, history.size() - 40).clear();
        }

        return response;
    }

    public Flux<String> chatStream(String message, String sessionId) {
        List<Message> history = sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());

        history.add(new UserMessage(message));

        StringBuilder fullResponse = new StringBuilder();

        return chatClient.prompt()
                .messages(history)
                .stream()
                .content()
                .doOnNext(fullResponse::append)
                .doOnComplete(() -> {
                    history.add(new AssistantMessage(fullResponse.toString()));
                    if (history.size() > 40) {
                        history.subList(0, history.size() - 40).clear();
                    }
                });
    }

    public void clearSession(String sessionId) {
        sessionHistory.remove(sessionId);
    }
}
