package com.jedistore.mcp.server.config;

import com.jedistore.api.generated.client.CustomersApi;
import com.jedistore.api.generated.client.OrdersApi;
import com.jedistore.api.generated.client.ProductsApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ApiClientConfig {

    @Value("${jedistore.api.base-url:http://localhost:8082}")
    private String apiBaseUrl;

    @Bean
    public RestClient pokeStoreRestClient() {
        ClientHttpRequestInterceptor authInterceptor = (request, body, execution) -> {
            String token = AuthTokenHolder.get();
            if (token != null) {
                request.getHeaders().add("Authorization", token);
            }
            return execution.execute(request, body);
        };

        return RestClient.builder()
                .baseUrl(apiBaseUrl)
                .requestInterceptor(authInterceptor)
                .build();
    }

    @Bean
    public ProductsApi productsApi(RestClient pokeStoreRestClient) {
        return createClient(pokeStoreRestClient, ProductsApi.class);
    }

    @Bean
    public CustomersApi customersApi(RestClient pokeStoreRestClient) {
        return createClient(pokeStoreRestClient, CustomersApi.class);
    }

    @Bean
    public OrdersApi ordersApi(RestClient pokeStoreRestClient) {
        return createClient(pokeStoreRestClient, OrdersApi.class);
    }

    private <T> T createClient(RestClient restClient, Class<T> clientClass) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();
        return factory.createClient(clientClass);
    }
}