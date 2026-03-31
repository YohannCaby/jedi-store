/**
 * Composable wrapper pour l'authentification
 * Expose les refs et actions du store de manière cohérente avec useChatBot
 */

import { computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useAuthStore } from '@/stores/auth'

export function useAuth() {
  const store = useAuthStore()

  // Extraire les refs réactives du store
  const {
    initialized,
    authenticated,
    loading,
    error,
    user,
    token,
    tokenParsed
  } = storeToRefs(store)

  // Getters computés
  const isAuthenticated = computed(() => store.isAuthenticated)
  const isInitialized = computed(() => store.isInitialized)
  const userInfo = computed(() => store.userInfo)
  const userRoles = computed(() => store.userRoles)

  return {
    // State (readonly refs)
    initialized,
    authenticated,
    loading,
    error,
    user,
    token,
    tokenParsed,

    // Getters
    isAuthenticated,
    isInitialized,
    userInfo,
    userRoles,

    // Actions
    init: store.init,
    login: store.login,
    logout: store.logout,
    refreshToken: store.doRefreshToken,
    getValidToken: store.getValidToken,
    checkRole: store.checkRole,
    hasAnyRole: store.hasAnyRole,
    hasAllRoles: store.hasAllRoles
  }
}
