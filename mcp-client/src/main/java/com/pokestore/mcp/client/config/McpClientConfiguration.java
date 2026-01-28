package com.pokestore.mcp.client.config;

import com.pokestore.mcp.client.McpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfiguration {

    @Value("${mcp.server.url:http://localhost:8081}")
    private String mcpServerUrl;

    @Bean
    @ConditionalOnMissingBean
    public McpClient mcpClient() {
        return new McpClient(mcpServerUrl);
    }
}
