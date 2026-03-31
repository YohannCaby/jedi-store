/**
 * main.ts
 *
 * Point d'entrée principal de l'application Vue.js 3
 * Configuration et initialisation de Vuetify 3 et Pinia
 */

import { createApp } from 'vue'
import vuetify from '@/plugins/vuetify'
import pinia from '@/plugins/pinia'
import App from '@/App.vue'
import { useAuthStore } from '@/stores/auth'

/**
 * Fonction d'initialisation asynchrone
 * Initialise l'authentification avant de monter l'application
 */
async function bootstrap() {
  // Créer l'application Vue
  const app = createApp(App)

  // Enregistrer les plugins
  app.use(pinia)
  app.use(vuetify)

  // Initialiser l'authentification Keycloak
  const authStore = useAuthStore()
  await authStore.init()

  // Monter l'application sur le DOM
  app.mount('#app')

  // Log de démarrage (uniquement en dev)
  if (import.meta.env.DEV) {
    console.log('🚀 Application Vue 3 + Vuetify 3 + SSE démarrée !')
    console.log('📡 Endpoint SSE: /api/chat → http://localhost:8080/chat')
    console.log('🔐 Keycloak:', authStore.isAuthenticated ? 'Authentifié' : 'Non authentifié')
  }
}

// Démarrer l'application
bootstrap().catch(console.error)
