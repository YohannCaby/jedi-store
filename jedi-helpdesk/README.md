# SSE Chat Monitor - Application Vue 3 + Vuetify 3

Application web temps réel utilisant Vue.js 3, Vuetify 3 et Server-Sent Events (SSE) pour afficher des messages en direct.

## Caractéristiques

- **Vue 3 Composition API** - Utilisation de `<script setup>` pour une syntaxe moderne et concise
- **Vuetify 3** - Framework UI Material Design avec composants personnalisables
- **Server-Sent Events (SSE)** - Connexion en temps réel avec reconnexion automatique
- **Thème Light/Dark** - Switch dynamique entre mode clair et sombre
- **TypeScript** - Typage strict pour une meilleure maintenabilité
- **Reconnexion automatique** - Gestion intelligente avec backoff exponentiel
- **Responsive Design** - Interface adaptée mobile et desktop

## Architecture du projet

```
poke-chatbot/
├── src/
│   ├── components/
│   │   ├── MessageList.vue          # Affichage des messages SSE
│   │   └── ThemeToggle.vue          # Bouton toggle thème
│   ├── composables/
│   │   └── useSSE.ts                # Logique SSE réutilisable
│   ├── types/
│   │   └── message.ts               # Types TypeScript
│   ├── plugins/
│   │   └── vuetify.ts               # Configuration Vuetify
│   ├── App.vue                      # Composant principal
│   ├── main.ts                      # Point d'entrée
│   └── vite-env.d.ts                # Types Vite
├── public/
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts                    # Configuration Vite + proxy
└── README.md
```

## Installation

### Prérequis

- Node.js >= 18.x
- npm >= 9.x

### Étapes d'installation

```bash
# Installer les dépendances
npm install
```

## Configuration

### Proxy SSE

Le projet utilise Vite avec un proxy configuré pour rediriger les requêtes `/api` vers le serveur backend SSE.

**Configuration dans `vite.config.ts` :**

```typescript
server: {
  proxy: {
    '^/api': {
      target: 'http://localhost:8080/chat',
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

**Endpoint SSE attendu :** `http://localhost:8080/chat`

### Format des messages SSE

Le serveur SSE doit envoyer des messages au format JSON suivant :

```json
data: {"message": "Nouvelle notification", "timestamp": "2026-01-30T23:00:00Z"}
```

**Structure des données :**

```typescript
interface Message {
  message: string      // Contenu du message
  timestamp: string    // Horodatage ISO 8601
}
```

## Utilisation

### Mode développement

```bash
npm run dev
```

L'application sera accessible sur `http://localhost:5173`

### Build de production

```bash
npm run build
```

Les fichiers optimisés seront générés dans le dossier `dist/`

### Prévisualiser le build

```bash
npm run preview
```

## Fonctionnalités

### 1. Connexion SSE en temps réel

- Connexion automatique au démarrage
- Affichage en temps réel des messages
- Indicateur de statut (connecté/déconnecté)

### 2. Reconnexion automatique

- Backoff exponentiel (1s, 2s, 4s, 8s, ..., max 30s)
- Maximum 10 tentatives de reconnexion
- Bouton de reconnexion manuelle

### 3. Interface utilisateur

- **Liste des messages** : Affichage avec icônes et horodatages
- **Scroll automatique** : Vers le bas lors de nouveaux messages
- **Animations** : Transitions fluides pour les nouveaux messages
- **Compteur** : Nombre total de messages reçus
- **Limitation** : Maximum 100 messages en mémoire (FIFO)

### 4. Thème Light/Dark

- Bouton toggle dans la barre de navigation
- Icônes animées (soleil/lune)
- Persistance du thème entre les sessions

### 5. Gestion des erreurs

- Affichage des erreurs de connexion
- Messages informatifs pour l'utilisateur
- Logging détaillé dans la console (mode dev)

## Composables

### `useSSE(url, options)`

Composable réutilisable pour gérer les connexions SSE.

**Paramètres :**

```typescript
interface SSEOptions {
  maxMessages?: number              // Défaut: 100
  maxReconnectAttempts?: number    // Défaut: 10
  reconnectDelay?: number          // Défaut: 1000ms
  maxReconnectDelay?: number       // Défaut: 30000ms
}
```

**Retour :**

```typescript
{
  messages: Readonly<Ref<Message[]>>
  connected: Readonly<Ref<boolean>>
  error: Readonly<Ref<string | null>>
  reconnectAttempts: Readonly<Ref<number>>
  clearMessages: () => void
  disconnect: () => void
  reconnect: () => void
}
```

**Exemple d'utilisation :**

```typescript
import { useSSE } from '@/composables/useSSE'

const { messages, connected, error, clearMessages } = useSSE('/api/chat', {
  maxMessages: 50,
  maxReconnectAttempts: 5
})
```

## Composants

### `MessageList.vue`

Affiche la liste des messages SSE avec animations et scroll automatique.

**Props :**

```typescript
interface Props {
  messages: readonly Message[]
}
```

### `ThemeToggle.vue`

Bouton pour basculer entre le thème clair et sombre.

**Pas de props** - Utilise directement `useTheme()` de Vuetify.

## Technologies utilisées

- **Vue 3.4** - Framework JavaScript progressif
- **Vuetify 3.5** - Framework UI Material Design
- **TypeScript 5.3** - Langage typé
- **Vite 5.0** - Build tool et dev server ultra-rapide
- **Material Design Icons** - Icônes (@mdi/font)

## Développement

### Structure des fichiers

- **`src/composables/`** - Logique réutilisable (Composition API)
- **`src/components/`** - Composants Vue
- **`src/types/`** - Définitions TypeScript
- **`src/plugins/`** - Configuration des plugins (Vuetify)

### Conventions de code

- Utilisation de `<script setup>` pour tous les composants
- Types TypeScript stricts
- Nommage en camelCase pour les variables/fonctions
- Nommage en PascalCase pour les composants
- Commentaires JSDoc pour les fonctions importantes

## Dépannage

### Le serveur SSE ne se connecte pas

1. Vérifiez que le backend SSE tourne sur `localhost:8080/chat`
2. Vérifiez la configuration du proxy dans `vite.config.ts`
3. Consultez la console du navigateur pour les erreurs

### Les messages ne s'affichent pas

1. Vérifiez le format JSON des messages SSE
2. Vérifiez que le champ `data:` contient `message` et `timestamp`
3. Consultez la console pour les erreurs de parsing

### Problème de reconnexion

1. Augmentez `maxReconnectAttempts` dans les options
2. Vérifiez les logs dans la console
3. Utilisez le bouton "Reconnecter" manuellement

## Auteur

Groupe Tech - 2026
