package com.jedistore.orchestrateur.config;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpLogging;
import org.springframework.context.annotation.Configuration;

/**
 * Gestionnaire des notifications de log émises par le MCP Server.
 * <p>
 * Le MCP Server envoie des messages de log via le protocole MCP
 * (via {@code ctx.info()} dans les outils). Ce handler les reçoit
 * côté client (orchestrateur) et les route vers le système de logging local.
 * </p>
 * <p>
 * L'annotation {@code @McpLogging(clients = "mcp-server")} lie ce handler
 * spécifiquement aux notifications provenant de la connexion nommée {@code mcp-server}
 * dans la configuration MCP client.
 * </p>
 */
@Configuration
public class McpLoggingHandler {
    private final Logger logger = LoggerFactory.getLogger(McpLoggingHandler.class);

    /**
     * Reçoit et logue les notifications de log du MCP Server.
     *
     * @param notification notification contenant le niveau de log et le message
     */
    @McpLogging(clients = "mcp-server")
    public void handleLoggingMessage(McpSchema.LoggingMessageNotification notification) {
        logger.info("Received log: {} - {}", notification.level(), notification.data());
    }
}