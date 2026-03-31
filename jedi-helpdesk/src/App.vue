<template>
  <v-app>
    <!-- Barre de navigation -->
    <v-app-bar color="primary" elevation="2">
      <v-app-bar-title class="d-flex align-center">
        <v-icon v-if="theme.global.current.value.dark" icon="mdi-death-star" class="mr-2" size="28" />
        <v-icon  v-else icon="mdi-sword" class="mr-2" size="28" />
        <span class="text-h6 font-weight-bold">Jedi Helpdesk</span>
      </v-app-bar-title>

      <v-spacer />

      <!-- Bouton connexion / Menu utilisateur -->
      <template v-if="!isAuthenticated">
        <v-btn
          variant="outlined"
          color="on-primary"
          prepend-icon="mdi-login"
          @click="login"
          class="mr-2"
        >
          Connexion
        </v-btn>
      </template>
      <template v-else>
        <v-menu>
          <template v-slot:activator="{ props: menuProps }">
            <v-tooltip :text="checkRole('ADMIN') ? 'Maître Jedi (Admin)' : 'Padawan (Utilisateur)'" location="bottom">
              <template v-slot:activator="{ props: tooltipProps }">
                <v-btn
                  v-bind="{ ...menuProps, ...tooltipProps }"
                  variant="text"
                  color="on-primary"
                  class="mr-2"
                >
                  <v-avatar size="32" color="secondary" class="mr-2">
                    {{ userInitials }}
                  </v-avatar>
                  {{ userInfo?.fullName }}
                  <v-icon icon="mdi-chevron-down" class="ml-1" />
                </v-btn>
              </template>
            </v-tooltip>
          </template>
          <v-list>
            <v-list-item prepend-icon="mdi-account">
              <v-list-item-title>{{ userInfo?.username }}</v-list-item-title>
              <v-list-item-subtitle v-if="userInfo?.email">{{ userInfo.email }}</v-list-item-subtitle>
            </v-list-item>
            <v-divider />
            <v-list-item
              prepend-icon="mdi-logout"
              @click="logout"
            >
              <v-list-item-title>Deconnexion</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </template>

      <!-- Toggle thème -->
      <ThemeToggle />
    </v-app-bar>

    <!-- Contenu principal -->
    <v-main style="padding-bottom: 80px;">
      <v-container class="py-8">
        <v-row justify="center">
          <v-col cols="12" md="10" lg="8">
            <!-- Card principale -->
            <v-card elevation="4" class="main-card" v-if="!isAuthenticated">
              <v-card-title>Authentification requise</v-card-title>
            </v-card>
            <v-card elevation="4" class="main-card" v-else>
              <!-- En-tête avec statut de connexion -->
              <v-card-title class="d-flex align-center justify-space-between pa-4">
                Jedi HelpDesk
              </v-card-title>

              <v-divider />

              <!-- Message d'erreur -->
              <v-alert
                v-if="error"
                type="warning"
                variant="tonal"
                class="ma-4"
                closable
              >
                <div class="d-flex align-center">
                  <v-icon icon="mdi-alert-circle" class="mr-2" />
                  {{ error }}
                </div>
              </v-alert>

              <!-- Liste des messages -->
              <MessageList :messages="messages" />

              <v-divider />

              <!-- Actions -->
              <v-card-actions class="pa-4 d-flex justify-space-between">
                <div class="text-caption text-medium-emphasis">
                  <v-icon icon="mdi-information" size="16" class="mr-1" />
                  Les messages sont limités à 100 entrées maximum
                </div>

                <v-btn
                  @click="clearMessages"
                  color="error"
                  variant="outlined"
                  prepend-icon="mdi-delete-sweep"
                  :disabled="messages.length === 0"
                >
                  Vider l'historique
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-container>
    </v-main>

    <!-- Champ de saisie sticky -->
    <v-footer app class="chat-input-footer pa-3">
      <v-container>
        <v-row align="center" no-gutters v-if="isAuthenticated">
          <v-col>
            <v-text-field
              v-model="userMessage"
              placeholder="Posez votre question..."
              variant="outlined"
              density="comfortable"
              hide-details
              @keyup.enter="handleSendMessage"
            />
          </v-col>
          <v-col cols="auto" class="ml-2">
            <v-btn
              @click="handleSendMessage"
              color="primary"
              icon="mdi-send"
              :loading="sending"
              :disabled="!userMessage.trim()"
            />
          </v-col>
        </v-row>
      </v-container>
    </v-footer>
  </v-app>
</template>

<script setup lang="ts">
import { ref, nextTick, computed } from 'vue'
import { useChatBot } from '@/composables/useChatBot.ts'
import { useAuth } from '@/composables/useAuth'
import MessageList from '@/components/MessageList.vue'
import ThemeToggle from '@/components/ThemeToggle.vue'
import { useTheme } from 'vuetify'

// Hook Vuetify pour gérer le thème
const theme = useTheme()

const {
  messages,
  error,
  sending,
  clearMessages,
  sendMessage
} = useChatBot()

const {
  isAuthenticated,
  userInfo,
  login: authLogin,
  logout: authLogout,
  checkRole
} = useAuth()

// Initiales de l'utilisateur pour l'avatar
const userInitials = computed(() => {
  if (!userInfo.value) return '?'
  const first = userInfo.value.firstName?.[0] ?? ''
  const last = userInfo.value.lastName?.[0] ?? ''
  return (first + last).toUpperCase() || userInfo.value.username?.[0]?.toUpperCase() || '?'
})

// Wrappers pour éviter que l'événement click soit passé aux fonctions auth
function login() {
  authLogin()
}

function logout() {
  authLogout()
}

const userMessage = ref('')

function scrollToBottom() {
  nextTick(() => {
    window.scrollTo({
      top: document.body.scrollHeight,
      behavior: 'smooth'
    })
  })
}

async function handleSendMessage() {
  if (!userMessage.value.trim()) return

  const message = userMessage.value.trim()
  userMessage.value = ''

  await sendMessage(message, {
    onUpdate: scrollToBottom
  })
}
</script>

<style scoped>
.main-card {
  border-radius: 12px;
  overflow: hidden;
}

code {
  background: rgba(var(--v-theme-primary), 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
}

/* Animation pour la card principale */
.main-card {
  animation: slideUp 0.5s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.chat-input-footer {
  background: rgb(var(--v-theme-surface));
  border-top: 1px solid rgba(var(--v-border-color), var(--v-border-opacity));
}
</style>
