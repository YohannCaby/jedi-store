package com.pokestore.orchestrateur.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de la mémoire de conversation Spring AI.
 */
@Configuration
public class ChatConfiguration {

    /**
     * Crée un bean {@link ChatMemory} en mémoire avec une fenêtre glissante de 20 messages.
     * <p>
     * {@code MessageWindowChatMemory} conserve les N derniers messages par session :
     * les plus anciens sont supprimés automatiquement quand la fenêtre est dépassée.
     * La mémoire est partagée entre toutes les sessions via leur {@code conversationId}.
     * </p>
     *
     * @return instance de mémoire de chat prête à l'emploi
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }
}
