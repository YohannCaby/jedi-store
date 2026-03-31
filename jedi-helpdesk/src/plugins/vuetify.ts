/**
 * plugins/vuetify.ts
 *
 * Configuration de Vuetify 3
 * Framework UI Material Design pour Vue.js
 */

import { createVuetify, ThemeDefinition } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Thème clair - Alliance Rebelle
// Inspiration : sable de Tatooine, orange/rouge du Starbird, bleu des X-Wings
const lightTheme: ThemeDefinition = {
  dark: false,
  colors: {
    background: '#FFF5E6',   // Sable chaud de Tatooine
    surface: '#FDE8C8',      // Beige Yavin IV
    primary: '#CC2936',      // Rouge Starbird de l'Alliance
    secondary: '#8B5E3C',    // Brun Jedi / Wookie
    error: '#8B0000',        // Rouge danger
    info: '#1B3A6B',         // Bleu X-Wing
    success: '#2E7D32',      // Vert forêt d'Endor
    warning: '#E65100',      // Orange flamme Rébellion
  }
}

// Thème sombre - Empire Galactique
// Inspiration : noir de l'espace, gris acier des Destroyers, rouge impérial, blanc Stormtrooper
const darkTheme: ThemeDefinition = {
  dark: true,
  colors: {
    background: '#080808',   // Vide de l'espace
    surface: '#1A1A1A',      // Gris Death Star
    primary: '#D0D0D0',      // Blanc Stormtrooper
    secondary: '#4A4A4A',    // Gris acier impérial
    error: '#CC0000',        // Rouge impérial
    info: '#707070',         // Gris uniforme officier
    success: '#1B5E20',      // Vert terminal holographique
    warning: '#BF360C',      // Orange alarme
  }
}

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  components,
  directives,
  theme: {
    defaultTheme: 'dark',
    themes: {
      light: lightTheme,
      dark: darkTheme,
    },
  },
  defaults: {
    VCard: {
      elevation: 2,
    },
    VBtn: {
      elevation: 0,
    },
  },
})
