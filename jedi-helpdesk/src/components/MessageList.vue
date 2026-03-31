<template>
  <v-card-text class="message-list-container">
    <!-- Message d'état vide -->
    <v-alert
      v-if="messages.length === 0"
      type="info"
      variant="tonal"
      class="mb-4"
    >
      <v-icon icon="mdi-information-outline" class="mr-2" />
      Aucun message.
    </v-alert>

    <!-- Liste des messages avec animation -->
    <v-list v-else class="message-list" lines="two">
      <transition-group name="message-fade">
        <v-list-item
          v-for="(message, index) in messages"
          :key="`${message.timestamp}-${index}`"
          class="message-item"
        >
          <template v-if="message.type === 'assistant'" v-slot:prepend>
            <v-avatar color="primary" size="40">
              <v-icon icon="mdi-message-text" />
            </v-avatar>
          </template>
          <template v-if="message.type === 'robot'" v-slot:append>
            <v-avatar color="primary" size="40">
              <v-icon icon="mdi-robot-happy-outline" />
            </v-avatar>
          </template>

          <div class="message-text" v-html="renderMarkdown(message.message)"></div>

          <v-list-item-subtitle class="message-timestamp">
            <v-icon icon="mdi-clock-outline" size="14" class="mr-1" />
            {{ formatTimestamp(message.timestamp) }}
          </v-list-item-subtitle>
        </v-list-item>
      </transition-group>
    </v-list>
  </v-card-text>
</template>

<script setup lang="ts">
import { marked } from 'marked'
import type { Message } from '@/types/message'

// Configuration de marked pour le rendu inline (sans balises <p> englobantes)
marked.setOptions({
  breaks: true,
  gfm: true
})

// Props
interface Props {
  messages: readonly Message[]
}

defineProps<Props>()

/**
 * Rend le markdown en HTML
 */
const renderMarkdown = (text: string): string => {
  if (!text) return ''
  return marked.parse(text) as string
}

/**
 * Formate le timestamp en format lisible français
 */
const formatTimestamp = (timestamp: string): string => {
  const date = new Date(timestamp)

  // Vérifier si la date est valide
  if (isNaN(date.getTime())) {
    return timestamp
  }

  // Format: "30/01/2026 à 23:00:05"
  return date.toLocaleString('fr-FR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
  })
}
</script>

<style scoped>
.message-list-container {
  padding: 16px;
}

.message-list {
  background: transparent;
}

.message-item {
  margin-bottom: 8px;
  padding: 12px;
  border-radius: 8px;
  background: rgba(var(--v-theme-surface), 0.5);
  transition: all 0.3s ease;
}

.message-item:hover {
  background: rgba(var(--v-theme-primary), 0.1);
}

.message-text {
  font-size: 16px;
  font-weight: 400;
  color: rgb(var(--v-theme-on-surface));
  word-wrap: break-word;
  line-height: 1.6;
}

/* Styles markdown */
.message-text :deep(p) {
  margin: 0 0 0.5em 0;
}

.message-text :deep(p:last-child) {
  margin-bottom: 0;
}

.message-text :deep(h1),
.message-text :deep(h2),
.message-text :deep(h3),
.message-text :deep(h4) {
  margin: 0.5em 0 0.3em 0;
  font-weight: 600;
}

.message-text :deep(h1) { font-size: 1.4em; }
.message-text :deep(h2) { font-size: 1.2em; }
.message-text :deep(h3) { font-size: 1.1em; }

.message-text :deep(ul),
.message-text :deep(ol) {
  margin: 0.5em 0;
  padding-left: 1.5em;
}

.message-text :deep(li) {
  margin: 0.2em 0;
}

.message-text :deep(code) {
  background: rgba(var(--v-theme-primary), 0.1);
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 0.9em;
}

.message-text :deep(pre) {
  background: rgba(var(--v-theme-on-surface), 0.05);
  padding: 12px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 0.5em 0;
}

.message-text :deep(pre code) {
  background: none;
  padding: 0;
}

.message-text :deep(blockquote) {
  border-left: 3px solid rgba(var(--v-theme-primary), 0.5);
  margin: 0.5em 0;
  padding-left: 1em;
  color: rgba(var(--v-theme-on-surface), 0.8);
}

.message-text :deep(a) {
  color: rgb(var(--v-theme-primary));
  text-decoration: none;
}

.message-text :deep(a:hover) {
  text-decoration: underline;
}

.message-text :deep(strong) {
  font-weight: 600;
}

.message-text :deep(em) {
  font-style: italic;
}

.message-timestamp {
  font-size: 12px;
  color: rgba(var(--v-theme-on-surface), 0.6);
  margin-top: 4px;
  display: flex;
  align-items: center;
}

/* Animations pour les nouveaux messages */
.message-fade-enter-active {
  transition: all 0.4s ease;
}

.message-fade-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.message-fade-enter-to {
  opacity: 1;
  transform: translateX(0);
}

</style>
