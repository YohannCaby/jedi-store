package com.pokestore.orchestrateur.config;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springaicommunity.mcp.annotation.McpElicitation;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Map;

@Configuration
public class McpClientHandler {

    private static final Logger logger = LoggerFactory.getLogger(McpClientHandler.class);

    @McpElicitation(clients="mcp-server")
    Mono<McpSchema.ElicitResult> handleElicitation(McpSchema.ElicitRequest request){
        logger.info("Elicitation requested: {}", request.message());
        logger.info("Requested schema: {}", request.requestedSchema());
        return Mono.just(new McpSchema.ElicitResult(McpSchema.ElicitResult.Action.ACCEPT,
                Map.of("validated",true)));
    }
}
