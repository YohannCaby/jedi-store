Voici votre **prompt final structuré en Markdown**, intégrant la nouvelle organisation des modules Maven, les dépendances, et les responsabilités de chaque module — prêt à être utilisé avec un LLM pour générer le code Spring Boot + Spring AI :

---

# 🧩 Prompt : Génération d’une application Spring Boot / Spring AI avec architecture hexagonale et modules Maven

> **Objectif** : Générer une application Java Spring Boot 3.5.10 avec Spring AI 1.1.2, respectant une **architecture hexagonale (ports & adapters)**, structurée en **5 modules Maven**, avec déploiement Docker et données en français.

---

## 📦 Structure Maven (5 modules)

| Module          | Description                                                                 | Livrable ? | Dépendances         |
|-----------------|-----------------------------------------------------------------------------|----------|---------------------|
| `core`          | Domaine : entités, value objects, use cases, ports (interfaces)                   | ❌       | —                   |
| `api`           | Adapter REST : controllers, DTOs, services REST                                 | ✅ JAR   | `core`              |
| `mcp-server`    | Adapter MCP : expose un endpoint `/mcp` via Spring AI + Ollama (mistral)           | ✅ JAR   | `core`              |
| `mcp-client`    | Client léger pour interagir avec `mcp-server` (pas de livrable)                    | ❌       | —              |
| `orchestrateur` | API REST + ChatClient + ChatMemory : interface avec le LLM, gère la conversation   | ✅ JAR   | `mcp-client` |

> ✅ Seuls les modules `api`, `mcp-server`, et `orchestrateur` génèrent un JAR exécutable.

---

## 🧾 Fonctionnalités

### Données (PostgreSQL)
- 100 clients (nom, email, adresse)
- 1000 commandes (date, client_id, statut, montant total)
- 100 produits (nom, description, prix, catégorie “peluches Pokémon”)

### API REST (`api` et `orchestrateur`)
- `GET /clients/{id}/commandes`
- `POST /commandes`
- `PUT /commandes/{id}/statut`
- `GET /produits`

### Serveur MCP (`mcp-server`)
- Endpoint : `POST /mcp` → reçoit une requête textuelle (ex: “Quelle est la commande la plus récente de Jean Dupont ?”)
- Utilise **Ollama en local** avec le modèle **ministral-3** (endpoint `http://localhost:11434`)
- Ne supporte pas les tools
- Expose le servise avec le protocole SSE

### Client MCP (`mcp-client`)
- Classe Java pour appeler le serveur MCP via HTTP (ex: `WebClient`)
- Utilisé par `orchestrateur`

### Orchestrateur (`orchestrateur`)
- Expose une API REST pour la **conversation** avec le LLM :
  - `POST /chat` → envoie un message et retourne la réponse avec mémoire (ChatMemory)
  - Utilise `ChatClient` de Spring AI + `mcp-client` pour interagir avec le serveur MCP
  - Gère la session utilisateur (ex: via `InMemoryChatMemory`)

---

## 🏗️ Architecture hexagonale

- **Domaine** (`core`) : entités, use cases, ports (interfaces)
- **Adapters** :
  - `api` : REST (controllers → use cases)
  - `mcp-server` : MCP (Spring AI → use cases)
  - `orchestrateur` : ChatClient + mémoire → use cases
- **Persistence** : JPA repositories dans `core` (ou dans `api` si nécessaire)

---

## 📄 Configuration

### `application.yml` (dans chaque module exécutable)
- `api` : config PostgreSQL, activer les endpoints REST
- `mcp-server` : config Spring AI (Ollama endpoint, modèle `mistral`), activer le MCP
- `orchestrateur` : config Spring AI (si utilisé directement), URL du `mcp-server`

### Tests
- Tests unitaires (JUnit + Mockito) pour les use cases dans `core`.
- Pas de test unitaires dans les autres modules.

---

## 🐳 Docker

- `Dockerfile` pour chaque module exécutable (`api`, `mcp-server`, `orchestrateur`)
- `docker-compose.yml` qui déploie :
  - PostgreSQL (avec volume pour les données)
  - `api` sur le port 8082
  - `mcp-server` sur le port 8081
  - `orchestrateur` sur le port 8080

---

## 📝 Notes

- Utiliser **Spring Boot 3.5.10+**, **Spring AI 1.1.2+**
- Utiliser **Spring Data JPA** pour l’accès aux données
- Utiliser **WebClient** pour les appels HTTP entre modules
- Utiliser **ChatMemory** pour la gestion des conversations dans `orchestrateur`
