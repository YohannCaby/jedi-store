package com.pokestore.shared;

import java.util.Comparator;

/**
 * Niveaux d'accès hiérarchiques utilisés pour le filtrage des outils MCP.
 * <p>
 * Convention de nommage des outils MCP : {@code NIVEAU_nomDeLAction}
 * (ex. {@code USER_getCustomerOrders}, {@code ADMIN_createOrder}).
 * L'orchestrateur extrait le préfixe, le valide via {@link #isValid(String)},
 * puis compare le niveau de l'utilisateur avec celui requis par l'outil.
 * </p>
 *
 * <p>Hiérarchie des niveaux (ordre croissant de privilèges) :</p>
 * <pre>
 *   ALL(0) &lt; USER(10) &lt; ADMIN(100) &lt; SUPER_ADMIN(MAX)
 * </pre>
 *
 * <p>Un utilisateur peut accéder à tous les outils dont le niveau est
 * inférieur ou égal au sien ({@code userLevel.compareTo(toolLevel) >= 0}).</p>
 */
public enum AuthAccessLevel implements Comparator<AuthAccessLevel> {

    /** Accès sans authentification — outils publics. */
    ALL(0),

    /** Accès utilisateur authentifié — consultation de ses propres données. */
    USER(10),

    /** Accès administrateur — modification des données (création, mise à jour). */
    ADMIN(100),

    /** Accès super-administrateur — opérations critiques réservées. */
    SUPER_ADMIN(Integer.MAX_VALUE);

    final int accessLevel;

    AuthAccessLevel(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    /**
     * Retourne la valeur numérique du niveau d'accès.
     * Plus la valeur est élevée, plus le niveau est privilégié.
     *
     * @return la valeur numérique du niveau
     */
    public int getLevel(){
        return this.accessLevel;
    }

    /**
     * Vérifie si une chaîne correspond à un niveau d'accès valide.
     * Utilisé par l'orchestrateur pour valider le préfixe d'un nom d'outil MCP.
     *
     * @param value la chaîne à valider (insensible à la casse)
     * @return {@code true} si la valeur correspond à une constante de cet enum
     */
    public static boolean isValid(String value){
        for(AuthAccessLevel o : AuthAccessLevel.values()) {
            if (value.toUpperCase().equals(o.name())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retourne le nom de la constante enum (identique à {@link #name()}).
     *
     * @return le nom de la constante
     */
    public String getName(){
        return this.name();
    }

    /**
     * Compare deux niveaux d'accès par leur valeur numérique.
     * Implémente {@link Comparator} pour permettre le tri par niveau croissant.
     *
     * @param o1 premier niveau
     * @param o2 second niveau
     * @return un entier négatif, zéro ou positif selon que o1 est inférieur, égal ou supérieur à o2
     */
    @Override
    public int compare(AuthAccessLevel o1, AuthAccessLevel o2) {
        return o1.getLevel() - o2.getLevel();
    }

}
