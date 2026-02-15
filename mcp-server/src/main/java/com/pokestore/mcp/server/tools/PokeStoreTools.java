package com.pokestore.mcp.server.tools;

import com.pokestore.api.generated.client.CustomersApi;
import com.pokestore.api.generated.client.OrdersApi;
import com.pokestore.api.generated.client.ProductsApi;
import com.pokestore.api.generated.model.*;
import com.pokestore.mcp.server.model.ValidationRequest;
import io.modelcontextprotocol.spec.McpSchema;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.context.McpAsyncRequestContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PokeStoreTools {

    ProductsApi productsApi;
    OrdersApi ordersApi;
    CustomersApi customersApi;

    private static final String GET_ALL_PRODUCTS = "USER_getAllProducts";
    private static final String GET_CUSTOMER_ORDERS = "USER_getCustomerOrders";
    private static final String CREATE_ORDER = "ADMIN_createOrder";
    private static final String UPDATE_ORDER_STATUS = "ADMIN_updateOrderStatus";
    private static final String GET_ORDER_DETAILS = "USER_getOrderDetails";
    private static final String SEARCH_PRODUCTS = "USER_searchProducts";
    private static final String SEARCH_CUSTOMERS = "USER_searchCustomer";

    public PokeStoreTools(ProductsApi productsApi,
                          OrdersApi ordersApi,
                          CustomersApi customerApi) {
        this.productsApi = productsApi;
        this.ordersApi = ordersApi;
        this.customersApi = customerApi;
    }

    @McpTool(name= GET_ALL_PRODUCTS, description = "Get all available products in the Poke Store")
    public String getAllProducts() {
        List<ProductDto> products = productsApi.getAllProducts().getBody();
        if (products == null || products.isEmpty()) {
            return "No products available.";
        }
        return products.stream()
                .map(p -> String.format("- %s (ID: %d) - %s - %.2f$ - Category: %s",
                        p.getName(), p.getId(), p.getDescription(), p.getPrice(), p.getCategory()))
                .collect(Collectors.joining("\n"));
    }

    @McpTool(name=GET_CUSTOMER_ORDERS, description = "Get orders for a specific customer by their ID")
    public Mono<String> getCustomerOrders(
            @ToolParam(description = "The customer ID") Long customerId) {
        return Mono.fromCallable(() -> {
            List<OrderDto> orders = customersApi.getOrdersByCustomerId(customerId).getBody();
            if (orders == null || orders.isEmpty()) {
                return "No orders found for customer ID: " + customerId;
            }
            return orders.stream()
                    .map(o -> String.format("Order #%d - Date: %s - Status: %s - Total: %.2f$",
                            o.getId(), o.getOrderDate(), o.getStatus(), o.getTotalAmount()))
                    .collect(Collectors.joining("\n"));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @McpTool(name=CREATE_ORDER, description = "Create a new order for a customer")
    public Mono<String> createOrder(
            @ToolParam(description = "The customer ID") Long customerId,
            @ToolParam(description = "List of product IDs to order") List<Long> productIds,
            @ToolParam(description = "Quantities for each product (in same order as productIds)") List<Integer> quantities) {
        return Mono.fromCallable(() -> {
            if (productIds.size() != quantities.size()) {
                return "Error: Number of product IDs must match number of quantities";
            }

            List<OrderLineRequest> lines = new java.util.ArrayList<>();
            for (int i = 0; i < productIds.size(); i++) {
                lines.add(new OrderLineRequest(productIds.get(i), quantities.get(i)));
            }

            CreateOrderRequest createOrderRequest = new CreateOrderRequest(customerId, lines);

            OrderDto order = ordersApi.createOrder(createOrderRequest).getBody();
            return String.format("Order created successfully! Order ID: %d, Total: %.2f$, Status: %s",
                    order.getId(), order.getTotalAmount(), order.getStatus());
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @McpTool(name= UPDATE_ORDER_STATUS, description = "Update the status of an existing order")
    public Mono<String> updateOrderStatus(
            @ToolParam(description = "The order ID") Long orderId,
            @ToolParam(description = "New status: IN_PROGRESS, DELIVERED, or CANCELLED") String status,
            McpAsyncRequestContext requestContext) {
        try {
            UpdateStatusRequest.StatusEnum newStatus = UpdateStatusRequest.StatusEnum.valueOf(status.toUpperCase());
            UpdateStatusRequest updateStatusRequestEntity = new UpdateStatusRequest(newStatus);
            return requestContext.elicit(
                    e -> e.message("Valider l'action oui/non"),
                    ValidationRequest.class
            ).flatMap(elicitResult -> {
                if (McpSchema.ElicitResult.Action.ACCEPT.equals(elicitResult.action())) {
                    boolean isValidated = elicitResult.structuredContent().isValidated();
                    if (isValidated) {
                        return Mono.fromCallable(() -> {
                            OrderDto order = ordersApi.updateOrderStatus(orderId, updateStatusRequestEntity).getBody();
                            return String.format("Order #%d status updated to: %s", order.getId(), order.getStatus());
                        }).subscribeOn(Schedulers.boundedElastic());
                    }
                }
                return Mono.just("L'information n'a pas été mise à jour.");
            });
        } catch (IllegalArgumentException e) {
            return Mono.just("Error: Invalid status. Use IN_PROGRESS, DELIVERED, or CANCELLED");
        } catch (Exception e) {
            return Mono.just("Error updating order: " + e.getMessage());
        }
    }

    @McpTool(name= GET_ORDER_DETAILS ,description = "Get details of a specific order by ID")
    public Mono<String> getOrderDetails(
            @ToolParam(description = "The order ID") Long orderId) {
        return Mono.fromCallable(() -> {
            OrderDto order = ordersApi.getOrderById(orderId).getBody();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Order #%d\n", order.getId()));
            sb.append(String.format("Date: %s\n", order.getOrderDate()));
            sb.append(String.format("Status: %s\n", order.getStatus()));
            sb.append(String.format("Customer: %s\n", order.getCustomer() != null ? order.getCustomer().getName() : "N/A"));
            sb.append("Items:\n");
            order.getLines().forEach(line ->
                    sb.append(String.format("  - %s x%d @ %.2f$ = %.2f$\n",
                            line.getProduct() != null ? line.getProduct().getName() : "Unknown",
                            line.getQuantity(),
                            line.getUnitPrice(),
                            line.getLineTotal())));
            sb.append(String.format("Total: %.2f$", order.getTotalAmount()));
            return sb.toString();
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @McpTool(name=SEARCH_PRODUCTS,description = "Search for products by name or category")
    public Mono<String> searchProducts(
            @ToolParam(description = "Search term for product name or category") String searchTerm) {
        return Mono.fromCallable(() -> {
            List<ProductDto> products = productsApi.getAllProducts().getBody();
            if (products == null) {
                return "No products found matching: " + searchTerm;
            }
            String term = searchTerm.toLowerCase();
            List<ProductDto> filtered = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(term)
                            || (p.getCategory() != null && p.getCategory().toLowerCase().contains(term))
                            || (p.getDescription() != null && p.getDescription().toLowerCase().contains(term)))
                    .toList();

            if (filtered.isEmpty()) {
                return "No products found matching: " + searchTerm;
            }

            return filtered.stream()
                    .map(p -> String.format("- %s (ID: %d) - %.2f$ - %s",
                            p.getName(), p.getId(), p.getPrice(), p.getCategory()))
                    .collect(Collectors.joining("\n"));
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @McpTool(name=SEARCH_CUSTOMERS, description = "Search customer(s) by name or email")
    public Mono<String> searchCustomer(
            @ToolParam(description = "Search by customer email") String email,
            @ToolParam(description = "Search by customer name") String name
    ) {
        return Mono.fromCallable(() -> {
            CustomersDto customers = customersApi.searchCustomers(name, email).getBody();
            if (customers == null || customers.getData().isEmpty()) {
                return "No customer(s) found";
            }
            return customers.getData().stream()
                    .map(CustomerDto::toString)
                    .collect(Collectors.joining("\n"));
        }).subscribeOn(Schedulers.boundedElastic());
    }
}