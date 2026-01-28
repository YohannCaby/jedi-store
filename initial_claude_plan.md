# Plan d'implémentation - Poke Store

## Vue d'ensemble

Application **Spring Boot 3.4.2** + **Spring AI 1.1.2** avec architecture hexagonale, structurée en **6 modules Maven**.

## Structure des modules

```
poke-store/
├── pom.xml (parent)
├── core/                    # Domaine (non exécutable)
├── bdd-connector/           # Accès BDD - JPA (non exécutable)
├── api/                     # REST adapter (JAR - port 8082)
├── mcp-server/              # MCP adapter (JAR - port 8081)
├── mcp-client/              # Client library (non exécutable)
├── orchestrateur/           # Chat orchestration (JAR - port 8080)
├── docker-compose.yml
└── .gitignore
```

## Ordre d'implémentation

### Phase 1: Configuration Maven et structure de base

1. **Créer le parent POM** (`pom.xml`)
   - Spring Boot 3.4.2, Spring AI 1.1.2
   - Gestion des dépendances centralisée
   - Déclaration des 6 modules

2. **Créer les 6 modules** avec leur `pom.xml` respectif

### Phase 2: Module Core (domaine) - Noms en anglais

**Entities:**
- `Customer` (id, name, email, address)
- `Product` (id, name, description, price, category)
- `Order` (id, orderDate, customer, status, totalAmount, lines)
- `OrderLine` (id, order, product, quantity, unitPrice)

**Value Objects:**
- `OrderStatus` (IN_PROGRESS, DELIVERED, CANCELLED)

**Input Ports (use cases):**
- `CustomerUseCase`: getOrdersByCustomerId(Long)
- `OrderUseCase`: createOrder(), updateStatus()
- `ProductUseCase`: getAllProducts()

**Output Ports (repositories):**
- `CustomerRepositoryPort`
- `OrderRepositoryPort`
- `ProductRepositoryPort`

**Tests unitaires:**
- `OrderUseCaseTest` (JUnit + Mockito)
- `ProductUseCaseTest`
- `CustomerUseCaseTest`

### Phase 3: Module BDD-Connector (nouveau)

**Structure:**
```
bdd-connector/src/main/java/com/pokestore/db/
├── entity/
│   ├── CustomerEntity.java
│   ├── ProductEntity.java
│   ├── OrderEntity.java
│   └── OrderLineEntity.java
├── repository/
│   ├── CustomerJpaRepository.java
│   ├── ProductJpaRepository.java
│   ├── OrderJpaRepository.java
│   └── OrderLineJpaRepository.java
├── adapter/
│   ├── CustomerRepositoryAdapter.java  # implémente CustomerRepositoryPort
│   ├── ProductRepositoryAdapter.java   # implémente ProductRepositoryPort
│   └── OrderRepositoryAdapter.java     # implémente OrderRepositoryPort
├── mapper/
│   ├── CustomerMapper.java
│   ├── ProductMapper.java
│   └── OrderMapper.java
└── config/
    └── DatabaseConfig.java
```

**Dépendances:**
- `core` (pour les ports et entités domaine)
- Spring Data JPA
- PostgreSQL driver
- Flyway

**Migrations Flyway:**
- `V1__create_tables.sql` - Schéma (tables en anglais: customers, products, orders, order_lines)
- `V2__insert_data.sql` - 100 customers, 100 products, 1000 orders

### Phase 4: Module API - Endpoints en anglais

**Structure:**
```
api/src/main/java/com/pokestore/api/
├── ApiApplication.java
├── controller/
│   ├── CustomerController.java
│   ├── OrderController.java
│   ├── ProductController.java
│   └── dto/
│       ├── CustomerDto.java
│       ├── OrderDto.java
│       ├── OrderLineDto.java
│       ├── ProductDto.java
│       ├── CreateOrderRequest.java
│       └── UpdateStatusRequest.java
├── mapper/
│   └── DtoMapper.java
└── config/
    └── WebConfig.java
```

**REST Endpoints (en anglais):**
- `GET /customers/{id}/orders`
- `POST /orders`
- `PUT /orders/{id}/status`
- `GET /products`

**Dépendances:**
- `core`
- `bdd-connector`

### Phase 5: Module MCP Server

**Structure:**
```
mcp-server/src/main/java/com/pokestore/mcp/server/
├── McpServerApplication.java
├── controller/
│   └── McpController.java (POST /mcp avec SSE)
├── service/
│   └── OllamaService.java
└── config/
    └── OllamaConfig.java
```

**Dépendances:**
- `core`
- `bdd-connector`

**Configuration:**
- Ollama base-url: `http://localhost:11434`
- Modèle: `ministral:3b`
- SSE pour les réponses en streaming

### Phase 6: Module MCP Client

**Structure:**
```
mcp-client/src/main/java/com/pokestore/mcp/client/
├── McpClient.java (WebClient)
└── config/
    └── McpClientConfiguration.java
```

**Dépendances:**
- Aucune dépendance interne (module autonome)
- Spring WebFlux (WebClient)

### Phase 7: Module Orchestrateur

**Structure:**
```
orchestrateur/src/main/java/com/pokestore/orchestrateur/
├── OrchestratorApplication.java
├── controller/
│   └── ChatController.java (POST /chat)
├── service/
│   └── ChatService.java (ChatClient + ChatMemory)
├── dto/
│   ├── ChatRequest.java
│   └── ChatResponse.java
└── config/
    └── ChatConfiguration.java
```

**Dépendances:**
- `mcp-client` uniquement
- Spring AI (ChatClient, ChatMemory)
- Spring Web

**ChatMemory:**
- `InMemoryChatMemory` avec `MessageWindowChatMemory`
- Conservation des 20 derniers messages par session

### Phase 8: Docker

**Dockerfiles** pour api, mcp-server, orchestrateur:
- Base: `eclipse-temurin:21-jre-alpine`

**docker-compose.yml:**
- PostgreSQL (port 5432, volume persistant)
- api (port 8082)
- mcp-server (port 8081)
- orchestrateur (port 8080)
- Network: `pokestore-network`

## Fichiers critiques

| Fichier | Rôle |
|---------|------|
| `pom.xml` | Parent POM avec gestion des dépendances |
| `core/.../OrderUseCase.java` | Interface métier principale |
| `bdd-connector/.../OrderRepositoryAdapter.java` | Implémentation JPA des ports |
| `bdd-connector/.../V1__create_tables.sql` | Schéma de base de données |
| `mcp-server/.../OllamaService.java` | Intégration Ollama |
| `orchestrateur/.../ChatService.java` | Logique de conversation |

## Dépendances entre modules

```
                    ┌─────────┐
                    │  core   │
                    └────┬────┘
                         │
                         ▼
                ┌──────────────┐
                │bdd-connector │
                └──────┬───────┘
                       │
           ┌───────────┴───────────┐
           │                       │
           ▼                       ▼
     ┌─────────┐            ┌────────────┐
     │   api   │            │ mcp-server │
     └─────────┘            └────────────┘


     ┌────────────┐
     │ mcp-client │
     └─────┬──────┘
           │
           ▼
    ┌──────────────┐
    │ orchestrateur│
    └──────────────┘
```

**Résumé des dépendances Maven:**
| Module | Dépend de |
|--------|-----------|
| `core` | aucun |
| `bdd-connector` | `core` |
| `api` | `core`, `bdd-connector` |
| `mcp-server` | `core`, `bdd-connector` |
| `mcp-client` | aucun |
| `orchestrateur` | `mcp-client` |

> **Note:** L'orchestrateur n'a PAS de dépendance vers `core` ou `bdd-connector`. Il communique uniquement via le MCP client.

## Vérification

1. **Build:** `mvn clean package -DskipTests`
2. **Tests:** `mvn test -pl core`
3. **Docker:** `docker-compose up -d`
4. **Test API:**
   ```bash
   curl http://localhost:8082/products
   curl http://localhost:8082/customers/1/orders
   ```
5. **Test Chat:**
   ```bash
   curl -X POST http://localhost:8080/chat \
     -H "Content-Type: application/json" \
     -d '{"sessionId":"test","message":"Bonjour"}'
   ```

## Notes techniques

- **Ollama:** Nécessite `ollama pull ministral:3b` avant démarrage
- **SSE:** Configuration WebFlux pour streaming
- **Transactions:** Gérées au niveau du module `bdd-connector`
- **Exceptions:** Domaines dans core, HTTP dans api
- **Orchestrateur:** Communication avec les données uniquement via MCP client (pas de JPA/DB direct)
- **Nommage:** Toutes les classes et endpoints en anglais (Customer, Order, Product, etc.)
