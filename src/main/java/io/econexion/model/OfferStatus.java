package io.econexion.model;

/**
 * Enumeration representing the status of an {@link Offer}.
 * <p>
 * An offer can be in one of the following states:
 * <ul>
 *     <li>{@link #PENDING} – The offer has been created but not yet accepted or rejected.</li>
 *     <li>{@link #ACCEPTED} – The offer has been accepted by the recipient.</li>
 *     <li>{@link #REJECTED} – The offer has been explicitly rejected.</li>
 * </ul>
 * This enum can be used to control the lifecycle of an offer within the system.
 */
public enum OfferStatus {

    /**
     * The offer is awaiting a decision (default state after creation).
     */
    PENDING,

    /**
     * The offer has been accepted by the recipient.
     */
    ACCEPTED,

    /**
     * The offer has been rejected by the recipient or system.
     */
    REJECTED

}
