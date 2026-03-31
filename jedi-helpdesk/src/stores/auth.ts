/**
 * Store Pinia pour l'authentification Keycloak
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getKeycloak, TOKEN_MIN_VALIDITY } from '@/services/keycloak'
import type { UserInfo } from '@/types/auth'

export const useAuthStore = defineStore('auth', () => {
  // State
  const initialized = ref(false)
  const authenticated = ref(false)
  const loading = ref(false)
  const error = ref<string | null>(null)
  const user = ref<UserInfo | null>(null)
  const token = ref<string | null>(null)
  const refreshTokenValue = ref<string | null>(null)
  const tokenParsed = ref<Record<string, unknown> | null>(null)

  // Intervalle de rafraîchissement
  let refreshInterval: ReturnType<typeof setInterval> | null = null

  // Getters
  const isAuthenticated = computed(() => authenticated.value)
  const isInitialized = computed(() => initialized.value)
  const userInfo = computed(() => user.value)
  const accessToken = computed(() => token.value)
  const userRoles = computed(() => user.value?.roles ?? [])

  /**
   * Extrait les informations utilisateur depuis le token Keycloak
   */
  function extractUserInfo(): UserInfo | null {
    const keycloak = getKeycloak()
    if (!keycloak.tokenParsed) return null

    const parsed = keycloak.tokenParsed as Record<string, unknown>
    const realmRoles = (parsed.realm_access as { roles?: string[] })?.roles ?? []
    const resourceRoles = Object.values(
      (parsed.resource_access as Record<string, { roles?: string[] }>) ?? {}
    ).flatMap(r => r.roles ?? [])

    return {
      id: parsed.sub as string,
      username: parsed.preferred_username as string,
      email: parsed.email as string | undefined,
      firstName: parsed.given_name as string | undefined,
      lastName: parsed.family_name as string | undefined,
      fullName: parsed.name as string || parsed.preferred_username as string,
      roles: [...new Set([...realmRoles, ...resourceRoles])]
    }
  }

  /**
   * Met à jour l'état depuis l'instance Keycloak
   */
  function updateState() {
    const keycloak = getKeycloak()
    authenticated.value = keycloak.authenticated ?? false
    token.value = keycloak.token ?? null
    refreshTokenValue.value = keycloak.refreshToken ?? null
    tokenParsed.value = keycloak.tokenParsed ?? null
    user.value = extractUserInfo()
  }

  /**
   * Démarre le rafraîchissement automatique du token
   */
  function startTokenRefresh() {
    if (refreshInterval) {
      clearInterval(refreshInterval)
    }

    // Rafraîchir toutes les 60 secondes
    refreshInterval = setInterval(async () => {
      try {
        await refreshToken()
      } catch (e) {
        console.error('Échec du rafraîchissement automatique:', e)
      }
    }, TOKEN_MIN_VALIDITY * 1000)
  }

  /**
   * Arrête le rafraîchissement automatique
   */
  function stopTokenRefresh() {
    if (refreshInterval) {
      clearInterval(refreshInterval)
      refreshInterval = null
    }
  }

  /**
   * Réinitialise l'état d'authentification
   */
  function clearState() {
    authenticated.value = false
    user.value = null
    token.value = null
    refreshTokenValue.value = null
    tokenParsed.value = null
    error.value = null
    stopTokenRefresh()
  }

  /**
   * Initialise Keycloak avec check-sso
   */
  async function init(): Promise<boolean> {
    if (initialized.value) return authenticated.value

    loading.value = true
    error.value = null

    try {
      const keycloak = getKeycloak()

      // Configurer les callbacks
      keycloak.onTokenExpired = async () => {
        console.log('Token expiré, rafraîchissement...')
        try {
          await refreshToken()
        } catch (e) {
          console.error('Échec du rafraîchissement après expiration:', e)
          clearState()
          error.value = 'Session expirée, veuillez vous reconnecter'
        }
      }

      keycloak.onAuthLogout = () => {
        console.log('Déconnexion détectée')
        clearState()
      }

      // Initialisation Keycloak sans auto-login
      // L'utilisateur cliquera sur le bouton "Connexion" manuellement
      const isAuth = await keycloak.init({
        checkLoginIframe: false
      })

      updateState()

      if (isAuth) {
        startTokenRefresh()
      }

      initialized.value = true
      return isAuth
    } catch (e) {
      console.error('Erreur initialisation Keycloak:', e)
      error.value = 'Erreur lors de l\'initialisation de l\'authentification'
      initialized.value = true
      return false
    } finally {
      loading.value = false
    }
  }

  /**
   * Redirige vers la page de connexion Keycloak
   */
  async function login(redirectUri?: string): Promise<void> {
    const keycloak = getKeycloak()
    // Utiliser l'URL courante complète pour éviter les erreurs 400
    const uri = redirectUri ?? window.location.href
    await keycloak.login({ redirectUri: uri })
  }

  /**
   * Déconnecte l'utilisateur
   */
  async function logout(redirectUri?: string): Promise<void> {
    stopTokenRefresh()
    const keycloak = getKeycloak()
    const uri = redirectUri ?? window.location.href
    await keycloak.logout({ redirectUri: uri })
    clearState()
  }

  /**
   * Rafraîchit le token d'accès
   */
  async function refreshToken(): Promise<boolean> {
    const keycloak = getKeycloak()

    if (!keycloak.authenticated) {
      return false
    }

    try {
      const refreshed = await keycloak.updateToken(TOKEN_MIN_VALIDITY)
      if (refreshed) {
        updateState()
        console.log('Token rafraîchi avec succès')
      }
      return true
    } catch (e) {
      console.error('Erreur rafraîchissement token:', e)
      clearState()
      error.value = 'Session expirée, veuillez vous reconnecter'
      return false
    }
  }

  /**
   * Retourne un token valide, en le rafraîchissant si nécessaire
   */
  async function getValidToken(): Promise<string | null> {
    const keycloak = getKeycloak()

    if (!keycloak.authenticated) {
      return null
    }

    // Rafraîchir si le token expire dans moins de 30 secondes
    try {
      await keycloak.updateToken(30)
      updateState()
      return keycloak.token ?? null
    } catch (e) {
      console.error('Impossible d\'obtenir un token valide:', e)
      clearState()
      error.value = 'Session expirée, veuillez vous reconnecter'
      return null
    }
  }

  /**
   * Vérifie si l'utilisateur a un rôle spécifique
   */
  function checkRole(role: string): boolean {
    return userRoles.value.includes(role)
  }

  /**
   * Vérifie si l'utilisateur a au moins un des rôles
   */
  function hasAnyRole(roles: string[]): boolean {
    return roles.some(role => checkRole(role))
  }

  /**
   * Vérifie si l'utilisateur a tous les rôles
   */
  function hasAllRoles(roles: string[]): boolean {
    return roles.every(role => checkRole(role))
  }

  return {
    // State
    initialized,
    authenticated,
    loading,
    error,
    user,
    token,
    refreshToken: refreshTokenValue,
    tokenParsed,

    // Getters
    isAuthenticated,
    isInitialized,
    userInfo,
    accessToken,
    userRoles,

    // Actions
    init,
    login,
    logout,
    doRefreshToken: refreshToken,
    getValidToken,
    checkRole,
    hasAnyRole,
    hasAllRoles
  }
})
