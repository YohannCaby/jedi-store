/**
 * composables/useChatBot.ts
 *
 * Composable réutilisable pour gérer le chatbot et les connexions SSE
 */

import { ref, readonly } from 'vue'
import type { Message } from '@/types/message'
import { useAuthStore } from '@/stores/auth'

interface SendMessageOptions {
  onUpdate?: () => void
}

export function useChatBot() {

  // État réactif
  const messages = ref<Message[]>([])
  const error = ref<string | null>(null)
  const sessionId = ref<string | null>(null)
  const sending = ref(false)


  /**
   * Ajoute un nouveau message à la liste
   * Limite le nombre de messages en mémoire (FIFO)
   */
  const addMessage = (message: Message) => {
    messages.value.push(message)
  }

  /**
   * Met à jour le dernier message (utile pour le streaming)
   */
  const updateLastMessage = (text: string) => {
    if (messages.value.length > 0) {
      messages.value[messages.value.length - 1].message = text
    }
  }

  /**
   * Vide tous les messages et réinitialise la session
   */
  const clearMessages = () => {
    messages.value = []
    sessionId.value = null
  }

  /**
   * Définit une erreur
   */
  const setError = (errorMessage: string | null) => {
    error.value = errorMessage
  }

  /**
   * Définit l'identifiant de session
   */
  const setSessionId = (id: string | null) => {
    sessionId.value = id
  }

  /**
   * Envoie un message et gère le streaming de la réponse
   */
  async function sendMessage(userMessage: string, options?: SendMessageOptions) {
    if (!userMessage.trim() || sending.value) return

    sending.value = true

    try {
      addMessage({
        message: userMessage,
        type: 'assistant',
        timestamp: new Date().toISOString()
      })

      addMessage({
        message: '',
        type: 'robot',
        timestamp: new Date().toISOString()
      })

      options?.onUpdate?.()

      if (!sessionId.value) {
        sessionId.value = crypto.randomUUID()
      }

      // Récupérer le token d'authentification si disponible
      const authStore = useAuthStore()
      const authToken = await authStore.getValidToken()

      const headers: Record<string, string> = {
        'Content-Type': 'application/json',
        'Accept': 'text/event-stream'
      }

      // Ajouter le header Authorization si authentifié
      if (authToken) {
        headers['Authorization'] = `Bearer ${authToken}`
      }

      const response = await fetch('/api/chat', {
        method: 'POST',
        headers,
        body: JSON.stringify({
          sessionId: sessionId.value,
          message: userMessage
        })
      })

      if (!response.ok) {
        throw new Error(`HTTP error: ${response.status}`)
      }

      const reader = response.body?.getReader()
      const decoder = new TextDecoder()

      if (!reader) {
        throw new Error('Response body is not readable')
      }

      let responseText = ''
      let buffer = ''

      while (true) {
        const { done, value } = await reader.read()
        if (done) break

        buffer += decoder.decode(value, { stream: true })
        const lines = buffer.split('\n')

        // Garder la dernière ligne incomplète dans le buffer
        buffer = lines.pop() ?? ''

        for (const line of lines) {
          if (line.startsWith('data:')) {
            const token = line.slice(5)
            responseText += token === '' ? '\n' : token
            updateLastMessage(responseText)
            options?.onUpdate?.()
          }
        }
      }

      // Traiter le reste du buffer
      if (buffer.startsWith('data:')) {
        const token = buffer.slice(5)
        responseText += token === '' ? '\n' : token
        updateLastMessage(responseText)
        options?.onUpdate?.()
      }
    } catch (e) {
      console.error('Erreur envoi message:', e)
      setError('Erreur lors de l\'envoi du message')
    } finally {
      sending.value = false
    }
  }

  // Retourne les valeurs réactives et les méthodes
  return {
    messages: readonly(messages),
    error: readonly(error),
    sessionId: readonly(sessionId),
    sending: readonly(sending),
    clearMessages,
    addMessage,
    updateLastMessage,
    setError,
    setSessionId,
    sendMessage
  }
}
