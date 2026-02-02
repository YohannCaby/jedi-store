package com.pokestore.mcp.server.tools;

import com.pokestore.api.generated.client.CustomersApi;
import com.pokestore.api.generated.client.OrdersApi;
import com.pokestore.api.generated.client.ProductsApi;
import com.pokestore.api.generated.model.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PokeStoreTools {

    ProductsApi productsApi;
    OrdersApi ordersApi;
    CustomersApi customersApi;

    public PokeStoreTools(ProductsApi productsApi,
                          OrdersApi ordersApi,
                          CustomersApi customerApi) {
        this.productsApi = productsApi;
        this.ordersApi = ordersApi;
        this.customersApi = customerApi;
    }

    @Tool(description = "Get all available products in the Poke Store")
    public String getAllProducts() {
        List<ProductDtoEntity> products = productsApi.getAllProducts().getBody();
        if (products.isEmpty()) {
            return "No products available.";
        }
        return products.stream()
                .map(p -> String.format("- %s (ID: %d) - %s - %.2f$ - Category: %s",
                        p.getName(), p.getId(), p.getDescription(), p.getPrice(), p.getCategory()))
                .collect(Collectors.joining("\n"));
    }

    @Tool(description = "Get orders for a specific customer by their ID")
    public String getCustomerOrders(
            @ToolParam(description = "The customer ID") Long customerId) {
        try {
            List<OrderDtoEntity> orders = customersApi.getOrdersByCustomerId(customerId).getBody();
            if (orders.isEmpty()) {
                return "No orders found for customer ID: " + customerId;
            }
            return orders.stream()
                    .map(o -> String.format("Order #%d - Date: %s - Status: %s - Total: %.2f$",
                            o.getId(), o.getOrderDate(), o.getStatus(), o.getTotalAmount()))
                    .collect(Collectors.joining("\n"));
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    @Tool(description = "Create a new order for a customer")
    public String createOrder(
            @ToolParam(description = "The customer ID") Long customerId,
            @ToolParam(description = "List of product IDs to order") List<Long> productIds,
            @ToolParam(description = "Quantities for each product (in same order as productIds)") List<Integer> quantities) {
        try {
            if (productIds.size() != quantities.size()) {
                return "Error: Number of product IDs must match number of quantities";
            }

            List<OrderLineRequestEntity> lines = new java.util.ArrayList<>();
            for (int i = 0; i < productIds.size(); i++) {
                lines.add(new OrderLineRequestEntity(productIds.get(i), quantities.get(i)));
            }

            CreateOrderRequestEntity createOrderRequest = new CreateOrderRequestEntity(customerId, lines);

            OrderDtoEntity order = ordersApi.createOrder(createOrderRequest).getBody();
            return String.format("Order created successfully! Order ID: %d, Total: %.2f$, Status: %s",
                    order.getId(), order.getTotalAmount(), order.getStatus());
        } catch (Exception e) {
            return "Error creating order: " + e.getMessage();
        }
    }

    @Tool(description = "Update the status of an existing order")
    public String updateOrderStatus(
            @ToolParam(description = "The order ID") Long orderId,
            @ToolParam(description = "New status: IN_PROGRESS, DELIVERED, or CANCELLED") String status) {
        try {
            UpdateStatusRequestEntity.StatusEnum newStatus = UpdateStatusRequestEntity.StatusEnum.valueOf(status.toUpperCase());
            UpdateStatusRequestEntity updateStatusRequestEntity = new UpdateStatusRequestEntity(newStatus);
            OrderDtoEntity order = ordersApi.updateOrderStatus(orderId, updateStatusRequestEntity).getBody();
            return String.format("Order #%d status updated to: %s", order.getId(), order.getStatus());
        } catch (IllegalArgumentException e) {
            return "Error: Invalid status. Use IN_PROGRESS, DELIVERED, or CANCELLED";
        } catch (Exception e) {
            return "Error updating order: " + e.getMessage();
        }
    }

    @Tool(description = "Get details of a specific order by ID")
    public String getOrderDetails(
            @ToolParam(description = "The order ID") Long orderId) {
        OrderDtoEntity order = ordersApi.getOrderById(orderId).getBody();
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
    }

    @Tool(description = "Search for products by name or category")
    public String searchProducts(
            @ToolParam(description = "Search term for product name or category") String searchTerm) {
        List<ProductDtoEntity> products = productsApi.getAllProducts().getBody();
        String term = searchTerm.toLowerCase();

        List<ProductDtoEntity> filtered = products.stream()
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
    }
}
