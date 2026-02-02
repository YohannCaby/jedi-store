package com.pokestore.mcp.server.config;

import com.pokestore.mcp.server.tools.PokeStoreTools;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpToolsConfig {

    @Bean
    public ToolCallbackProvider pokeStoreToolCallbackProvider(PokeStoreTools pokeStoreTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(pokeStoreTools)
                .build();
    }
}
