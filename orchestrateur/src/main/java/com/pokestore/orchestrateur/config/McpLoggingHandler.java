package com.pokestore.orchestrateur.config;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class McpLoggingHandler {
    private final Logger logger = LoggerFactory.getLogger(McpLoggingHandler.class);

    @McpLogging(clients = "mcp-server")
    public Mono<Void> handleLoggingMessage(McpSchema.LoggingMessageNotification notification) {
        logger.info("Received log: {} - {}", notification.level(), notification.data());
        return Mono.empty();
    }
}
