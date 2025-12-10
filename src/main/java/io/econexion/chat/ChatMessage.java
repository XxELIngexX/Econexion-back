package io.econexion.chat;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity representing a single message within a chat conversation.
 * <p>
 * Each {@link ChatMessage} belongs to a {@link ChatConversation} and stores
 * the sender, the textual content, and the creation timestamp.
 * </p>
 */
@Entity(name = "ChatMessage")
@Table(
        name = "chat_messages",
        indexes = @Index(
                name = "idx_chat_msg_conv_created",
                columnList = "conversation_id, created_at"
        )
)
public class ChatMessage {

    /**
     * Primary key of the message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Conversation to which this message belongs.
     * <p>
     * The association is mandatory and lazily loaded.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ChatConversation conversation;

    /**
     * Identifier of the user who sent this message.
     */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /**
     * Text content of the message.
     */
    @Column(name = "text", nullable = false, length = 5000)
    private String text;

    /**
     * Timestamp indicating when the message was created.
     * <p>
     * This value is automatically set on persist and is not updatable.
     * </p>
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Lifecycle callback executed before the entity is persisted.
     * <p>
     * Initializes {@link #createdAt} with the current instant.
     * </p>
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // === Getters and setters ===

    /**
     * @return the message ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the conversation this message belongs to
     */
    public ChatConversation getConversation() {
        return conversation;
    }

    /**
     * Sets the conversation this message belongs to.
     *
     * @param conversation the {@link ChatConversation} to associate
     */
    public void setConversation(ChatConversation conversation) {
        this.conversation = conversation;
    }

    /**
     * @return the sender's user ID
     */
    public Long getSenderId() {
        return senderId;
    }

    /**
     * Sets the sender's user ID.
     *
     * @param senderId the sender ID to set
     */
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the text content of this message
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text content of this message.
     *
     * @param text the message text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the creation timestamp of this message
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of this message.
     * <p>
     * Normally this is managed automatically by {@link #onCreate()}.
     * </p>
     *
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
