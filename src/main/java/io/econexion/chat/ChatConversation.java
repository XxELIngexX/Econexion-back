package io.econexion.chat;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity representing a chat conversation between two participants
 * around a specific offer.
 * <p>
 * This entity stores the participants, the associated offer,
 * and audit timestamps for creation and last update.
 * </p>
 */
@Entity(name = "ChatConversation")
@Table(
        name = "chat_conversations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"offer_id", "participant1_id", "participant2_id"}),
        indexes = {
                @Index(name = "idx_chat_conv_offer", columnList = "offer_id"),
                @Index(name = "idx_chat_conv_updated", columnList = "updated_at")
        }
)
public class ChatConversation {

    /**
     * Primary key of the conversation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifier of the offer associated with this conversation.
     */
    @Column(name = "offer_id", nullable = false)
    private Long offerId;

    /**
     * Identifier of the first participant in the conversation.
     * <p>
     * The pair (participant1Id, participant2Id) is normalized to ensure
     * uniqueness and avoid duplicate conversations.
     * </p>
     */
    @Column(name = "participant1_id", nullable = false)
    private Long participant1Id;

    /**
     * Identifier of the second participant in the conversation.
     */
    @Column(name = "participant2_id", nullable = false)
    private Long participant2Id;

    /**
     * Timestamp indicating when the conversation was created.
     * <p>
     * This value is set automatically on persist and is not updatable.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp indicating when the conversation was last updated.
     * <p>
     * This value is set automatically on persist and on each update.
     * </p>
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Lifecycle callback executed before the entity is persisted.
     * <p>
     * Initializes {@link #createdAt} and {@link #updatedAt} with the
     * current instant.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Lifecycle callback executed before the entity is updated.
     * <p>
     * Refreshes {@link #updatedAt} with the current instant.
     * </p>
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    /**
     * Checks whether the given user ID is one of the participants
     * of this conversation.
     *
     * @param userId the user ID to check
     * @return {@code true} if the user is participant1 or participant2,
     *         {@code false} otherwise
     */
    public boolean involvesUser(Long userId) {
        return userId != null && (userId.equals(participant1Id) || userId.equals(participant2Id));
    }

    // === Getters and setters ===

    /**
     * @return the conversation ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the conversation ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the offer ID associated with this conversation
     */
    public Long getOfferId() {
        return offerId;
    }

    /**
     * @param offerId the offer ID to associate with this conversation
     */
    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    /**
     * @return the first participant's user ID
     */
    public Long getParticipant1Id() {
        return participant1Id;
    }

    /**
     * @param participant1Id the first participant's user ID to set
     */
    public void setParticipant1Id(Long participant1Id) {
        this.participant1Id = participant1Id;
    }

    /**
     * @return the second participant's user ID
     */
    public Long getParticipant2Id() {
        return participant2Id;
    }

    /**
     * @param participant2Id the second participant's user ID to set
     */
    public void setParticipant2Id(Long participant2Id) {
        this.participant2Id = participant2Id;
    }

    /**
     * @return the creation timestamp of this conversation
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * @return the last update timestamp of this conversation
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt the last update timestamp to set
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
