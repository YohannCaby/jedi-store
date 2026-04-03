package com.jedistore.mcp.server.tools;

import com.jedistore.api.generated.client.CustomersApi;
import com.jedistore.api.generated.client.OrdersApi;
import com.jedistore.api.generated.client.ProductsApi;
import com.jedistore.api.generated.model.*;

import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.context.McpSyncRequestContext;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Outils MCP exposés par le serveur au LLM via le protocole MCP (Streamable HTTP).
 * <p>
 * Chaque méthode annotée {@code @McpTool} devient un outil invocable par le LLM
 * (via l'orchestrateur). Le nom de l'outil suit la convention {@code NIVEAU_action}
 * qui permet à l'orchestrateur de filtrer les outils selon le rôle de l'utilisateur.
 * </p>
 *
 * <p>Outils disponibles et niveaux d'accès requis :</p>
 * <ul>
 *   <li>{@code USER_getCustomerOrders}  — liste les commandes d'un client</li>
 *   <li>{@code USER_getOrderDetails}    — détail complet d'une commande</li>
 *   <li>{@code USER_searchProducts}     — recherche dans le catalogue</li>
 *   <li>{@code USER_searchCustomer}     — recherche de clients par nom/email</li>
 *   <li>{@code ADMIN_createOrder}       — création d'une commande (niveau ADMIN)</li>
 *   <li>{@code ADMIN_updateOrderStatus} — mise à jour du statut avec élicitation (niveau ADMIN)</li>
 * </ul>
 *
 * <p>Les outils n'accèdent pas directement à la base de données : ils passent
 * par les clients HTTP générés depuis l'OpenAPI spec du module {@code api}
 * ({@link ProductsApi}, {@link OrdersApi}, {@link CustomersApi}).</p>
 */
@Service
public class JediStoreTools {

    ProductsApi productsApi;
    OrdersApi ordersApi;
    CustomersApi customersApi;

    private static final String GET_CUSTOMER_ORDERS = "USER_getCustomerOrders";
    private static final String UPDATE_ORDER_STATUS = "ADMIN_updateOrderStatus";
    private static final String GET_ORDER_DETAILS = "USER_getOrderDetails";
    private static final String SEARCH_PRODUCTS = "USER_searchProducts";
    private static final String SEARCH_CUSTOMERS = "USER_searchCustomer";

    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6);
    }

    public JediStoreTools(ProductsApi productsApi,
                          OrdersApi ordersApi,
                          CustomersApi customerApi) {
        this.productsApi = productsApi;
        this.ordersApi = ordersApi;
        this.customersApi = customerApi;
    }

    @McpTool(name=GET_CUSTOMER_ORDERS, description = "Récupère les commandes d'un client spécifique par son identifiant")
    public String getCustomerOrders(@ToolParam(description = "L'identifiant du client") Long customerId, McpSyncRequestContext ctx, McpSyncServerExchange exchange) {
        String token = generateToken();
        ctx.info(String.format("Use Tool %s with params : customerId : %s", GET_CUSTOMER_ORDERS, customerId));
        exchange.progressNotification(new McpSchema.ProgressNotification(token,0,100.0,GET_CUSTOMER_ORDERS + " - Init"));
        List<OrderDto> orders = customersApi.getOrdersByCustomerId(customerId).getBody();
        if (orders == null || orders.isEmpty()) {
            exchange.progressNotification(new McpSchema.ProgressNotification(token,100.0,100.0,GET_CUSTOMER_ORDERS + " - End"));
            return "No orders found for customer ID: " + customerId;
        }
        exchange.progressNotification(new McpSchema.ProgressNotification(token,100.0,100.0,GET_CUSTOMER_ORDERS + " - End"));
        return orders.stream()
                .map(o -> String.format("Order #%d - Date: %s - Status: %s - Total: %.2f$",
                        o.getId(), o.getOrderDate(), o.getStatus(), o.getTotalAmount()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Met à jour le statut d'une commande avec une étape de validation humaine (élicitation MCP).
     * <p>
     * L'élicitation ({@code ctx.elicit}) envoie une requête de confirmation au client MCP
     * (l'orchestrateur) avant d'effectuer la modification. Si l'utilisateur refuse ou
     * ne valide pas, la mise à jour est annulée.
     * </p>
     *
     * @param orderId     identifiant de la commande à modifier
     * @param status      nouveau statut : {@code IN_PROGRESS}, {@code DELIVERED} ou {@code CANCELLED}
     * @param ctx         contexte de la requête MCP (logging, élicitation)
     * @param exchange    échange MCP courant (notifications de progression)
     * @return message décrivant le résultat de l'opération
     */
    @McpTool(name= UPDATE_ORDER_STATUS, description = "Met à jour le statut d'une commande existante")
    public String updateOrderStatus(
            @ToolParam(description = "L'identifiant de la commande") Long orderId,
            @ToolParam(description = "Nouveau statut : IN_PROGRESS (en cours), DELIVERED (livré) ou CANCELLED (annulé)") String status,
            McpSyncRequestContext ctx, McpSyncServerExchange exchange) {
        String token = generateToken();
        ctx.info(String.format("Use Tool %s with params: orderId: %s, status: %s", UPDATE_ORDER_STATUS,orderId,status));
        exchange.progressNotification(new McpSchema.ProgressNotification(token,0,100.0,UPDATE_ORDER_STATUS + " - Init"));
        try {
            UpdateStatusRequest.StatusEnum newStatus = UpdateStatusRequest.StatusEnum.valueOf(status.toUpperCase());
            UpdateStatusRequest updateStatusRequestEntity = new UpdateStatusRequest(newStatus);
            OrderDto order = ordersApi.updateOrderStatus(orderId,updateStatusRequestEntity).getBody();
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Order #%d\n", order.getId()));
            sb.append(String.format("Date: %s\n", order.getOrderDate()));
            sb.append(String.format("Status: %s\n", order.getStatus()));
        exchange.progressNotification(new McpSchema.ProgressNotification(token,100.0,100.0,UPDATE_ORDER_STATUS + " - End"));
            return String.format("L'information été mise à jour: %s", sb);
        } catch (IllegalArgumentException e) {
            exchange.progressNotification(new McpSchema.ProgressNotification(token,100.0,100.0,UPDATE_ORDER_STATUS + " - Error Enum"));
            return "Error: Invalid status. Use IN_PROGRESS, DELIVERED, or CANCELLED";
        } catch (Exception e) {
            exchange.progressNotification(new McpSchema.ProgressNotification(token,100.0,100.0,UPDATE_ORDER_STATUS + " - Error"));
            return "Error updating order: " + e.getMessage();
        }
    }

    @McpTool(name= GET_ORDER_DETAILS ,description = "Récupère le détail complet d'une commande par son identifiant")
    public String getOrderDetails(
            @ToolParam(description = "L'identifiant de la commande") Long orderId, McpSyncRequestContext ctx, McpSyncServerExchange exchange) {
        String token = generateToken();
        ctx.info(String.format("Use Tool %s with param orderId: %s", GET_ORDER_DETAILS, orderId));
        exchange.progressNotification(new McpSchema.ProgressNotification(token,0,100.0,GET_ORDER_DETAILS + " - Init"));
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
        exchange.progressNotification(new McpSchema.ProgressNotification(token,100.0,100.0,GET_ORDER_DETAILS + " - End"));
        return sb.toString();
    }

    @McpTool(name=SEARCH_PRODUCTS,description = "Recherche des produits par nom ou catégorie")
    public String searchProducts(
            @ToolParam(description = "Terme de recherche pour le nom ou la catégorie du produit") String searchTerm, McpSyncRequestContext ctx, McpSyncServerExchange exchange) {
        String token = generateToken();
        ctx.info(String.format("Use Tool %s, with param %s", SEARCH_PRODUCTS,searchTerm));
        exchange.progressNotification(new McpSchema.ProgressNotification(token,0,100.0,SEARCH_PRODUCTS + " - Init"));
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
        exchange.progressNotification(new McpSchema.ProgressNotification(token,100.0,100.0,SEARCH_PRODUCTS + " - End"));
        return filtered.stream()
                .map(p -> String.format("- %s (ID: %d) - %.2f$ - %s",
                        p.getName(), p.getId(), p.getPrice(), p.getCategory()))
                .collect(Collectors.joining("\n"));
    }

    @McpTool(name=SEARCH_CUSTOMERS, description = "Recherche un ou plusieurs clients par nom ou email")
    public String searchCustomer(
            @ToolParam(description = "Recherche par email du client") String email,
            @ToolParam(description = "Recherche par nom du client (recherche partielle)") String name, McpSyncRequestContext ctx, McpSyncServerExchange exchange
    ) {
        String token = generateToken();
        ctx.info(String.format("Use Tool %s params email: %s, name: %s", SEARCH_CUSTOMERS, email, name));
        exchange.progressNotification(new McpSchema.ProgressNotification(token,0,100.0,SEARCH_CUSTOMERS + " - Init"));
        CustomersDto customers = customersApi.searchCustomers(name, email).getBody();
        if (customers == null || customers.getData().isEmpty()) {
            return "No customer(s) found";
        }
        exchange.progressNotification(new McpSchema.ProgressNotification(token,100.0,100.0,SEARCH_CUSTOMERS + " - End"));
        return customers.getData().stream()
                .map(CustomerDto::toString)
                .collect(Collectors.joining("\n"));
    }
}