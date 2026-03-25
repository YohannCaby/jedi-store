package com.jedistore.core.domain.valueobject;

/**
 * Value object représentant le cycle de vie d'une commande.
 * <p>
 * Les transitions autorisées sont gérées au niveau métier par {@code OrderService}.
 * La valeur est stockée en base sous forme de chaîne (EnumType.STRING).
 * </p>
 */
public enum OrderStatus {
    /** Commande créée et en cours de traitement. */
    IN_PROGRESS,
    /** Commande expédiée et remise au client. */
    DELIVERED,
    /** Commande annulée avant expédition. */
    CANCELLED
}
