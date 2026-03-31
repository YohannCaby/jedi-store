/**
 * Types pour l'authentification Keycloak
 */

export interface UserInfo {
  id: string
  username: string
  email?: string
  firstName?: string
  lastName?: string
  fullName: string
  roles: string[]
}

export interface AuthState {
  initialized: boolean
  authenticated: boolean
  user: UserInfo | null
  token: string | null
  refreshToken: string | null
}

export interface KeycloakConfig {
  url: string
  realm: string
  clientId: string
}
