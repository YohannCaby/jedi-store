package com.pokestore.orchestrateur.config;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpProgress;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpProgressHandler {
    Logger logger = LoggerFactory.getLogger(McpProgressHandler.class);
    @McpProgress(clients="mcp-server")
    public void handleProgressNotification(McpSchema.ProgressNotification progress){
        logger.info("Progression [token:{}] : message : {}, progression :{}%",progress.progressToken().toString(),progress.message(),progress.progress());
    }
}
