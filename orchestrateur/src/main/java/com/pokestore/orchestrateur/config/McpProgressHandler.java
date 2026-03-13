package com.pokestore.orchestrateur.config;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpProgress;
import org.springframework.context.annotation.Configuration;

/**
 * Gestionnaire des notifications de progression émises par les outils MCP.
 * <p>
 * Chaque outil du MCP Server envoie des notifications de progression via
 * {@code exchange.progressNotification(...)} pour indiquer son avancement
 * (0% → 100%). Ce handler reçoit ces notifications côté orchestrateur
 * et les logue pour le monitoring.
 * </p>
 * <p>
 * Utile pour suivre l'exécution des outils à longue durée et diagnostiquer
 * les lenteurs ou les erreurs lors des appels MCP.
 * </p>
 */
@Configuration
public class McpProgressHandler {
    Logger logger = LoggerFactory.getLogger(McpProgressHandler.class);

    /**
     * Reçoit les notifications de progression d'un outil MCP en cours d'exécution.
     *
     * @param progress notification contenant le token de progression, le message et le pourcentage
     */
    @McpProgress(clients="mcp-server")
    public void handleProgressNotification(McpSchema.ProgressNotification progress){
        logger.info("Progression [token:{}] : message : {}, progression :{}%",progress.progressToken().toString(),progress.message(),progress.progress());
    }
}
