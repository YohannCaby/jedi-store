package com.pokestore.mcp.server.config;

import com.pokestore.api.generated.client.CustomersApi;
import com.pokestore.api.generated.client.OrdersApi;
import com.pokestore.api.generated.client.ProductsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ApiClientConfig {

    @Value("${pokestore.api.base-url:http://localhost:8082}")
    private String apiBaseUrl;

    @Bean
    public WebClient pokeStoreWebClient() {
        return WebClient.builder()
                .baseUrl(apiBaseUrl)
                .build();
    }

    @Bean
    public ProductsApi productsApi(WebClient pokeStoreWebClient) {
        return createClient(pokeStoreWebClient, ProductsApi.class);
    }

    @Bean
    public CustomersApi customersApi(WebClient pokeStoreWebClient) {
        return createClient(pokeStoreWebClient, CustomersApi.class);
    }

    @Bean
    public OrdersApi ordersApi(WebClient pokeStoreWebClient) {
        return createClient(pokeStoreWebClient, OrdersApi.class);
    }

    private <T> T createClient(WebClient webClient, Class<T> clientClass) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(webClient))
                .build();
        return factory.createClient(clientClass);
    }
}