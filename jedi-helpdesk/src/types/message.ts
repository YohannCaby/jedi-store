/**
 * Interface représentant un message SSE reçu du serveur
 */
export interface Message {
  type: 'assistant' | 'robot'
  message: string
  timestamp: string
}
