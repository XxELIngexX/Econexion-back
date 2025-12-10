package io.econexion.model;

/**
 * Enumeration representing the different roles a {@link User} can have within the system.
 * <p>
 * Roles are used for access control and permission management.
 * </p>
 */
public enum Role {

    /**
     * User who purchases or requests materials/services.
     */
    BUYER,

    /**
     * User who creates posts and offers goods or services.
     */
    SELLER,

    /**
     * System administrator with elevated privileges.
     */
    ADMIN
}
