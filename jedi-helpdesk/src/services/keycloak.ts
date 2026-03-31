/**
 * Service Keycloak singleton
 */

import Keycloak from 'keycloak-js'
import type { KeycloakConfig } from '@/types/auth'

// Configuration Keycloak
const keycloakConfig: KeycloakConfig = {
  url: 'http://localhost:8888',
  realm: 'coruscant',
  clientId: 'helpdesk'
}

// Durée avant expiration pour déclencher le rafraîchissement (en secondes)
export const TOKEN_MIN_VALIDITY = 60

// Instance singleton
let keycloakInstance: Keycloak | null = null

/**
 * Retourne l'instance Keycloak singleton
 */
export function getKeycloak(): Keycloak {
  if (!keycloakInstance) {
    keycloakInstance = new Keycloak(keycloakConfig)
  }
  return keycloakInstance
}

export { keycloakConfig }
