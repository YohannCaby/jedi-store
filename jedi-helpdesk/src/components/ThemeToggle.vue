<template>
  <v-tooltip :text="tooltipText" location="bottom">
    <template v-slot:activator="{ props }">
      <v-btn
        v-bind="props"
        :icon="themeIcon"
        @click="toggleTheme"
        variant="text"
        :aria-label="tooltipText"
      />
    </template>
  </v-tooltip>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useTheme } from 'vuetify'

// Hook Vuetify pour gérer le thème
const theme = useTheme()

/**
 * Bascule entre le thème clair et sombre
 */
const toggleTheme = () => {
  theme.global.name.value = theme.global.current.value.dark ? 'light' : 'dark'
}

/**
 * Icône correspondant au thème actuel
 */
const themeIcon = computed(() => {
  // En thème sombre (Empire) : soleil = rejoindre la Lumière
  // En thème clair (Rébellion) : lune = basculer vers l'Obscur
  return theme.global.current.value.dark ? 'mdi-lightbulb-on-10' : 'mdi-weather-night'
})

/**
 * Texte du tooltip
 */
const tooltipText = computed(() => {
  return theme.global.current.value.dark ? 'Rejoins la Force' : 'Passes du côté obscure'
})
</script>

<style scoped>
/* Animations fluides pour le changement d'icône */
.v-btn {
  transition: transform 0.3s ease;
}

.v-btn:hover {
  transform: rotate(20deg);
}
</style>
