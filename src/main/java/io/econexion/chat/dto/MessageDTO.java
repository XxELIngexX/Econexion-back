package io.econexion.chat.dto;

import java.time.Instant;

/**
 * Data Transfer Object representing a single chat message within a conversation.
 * <p>
 * This DTO is immutable: all fields are {@code final} and initialized via constructor.
 * It is typically returned by the backend when listing messages or sending a new one.
 * </p>
 */
public class MessageDTO {

    /**
     * Unique identifier of the message.
     */
    private final Long id;

    /**
     * Identifier of the user who sent the message.
     */
    private final Long senderId;

    /**
     * Message text content.
     */
    private final String text;

    /**
     * Timestamp representing when the message was created.
     */
    private final Instant createdAt;

    /**
     * Constructs a new {@link MessageDTO} with all required fields.
     *
     * @param id        the message ID
     * @param senderId  the ID of the sender
     * @param text      the message content
     * @param createdAt timestamp of creation
     */
    public MessageDTO(Long id, Long senderId, String text, Instant createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.text = text;
        this.createdAt = createdAt;
    }

    /**
     * @return the message ID
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the sender's user ID
     */
    public Long getSenderId() {
        return senderId;
    }

    /**
     * @return the text content of the message
     */
    public String getText() {
        return text;
    }

    /**
     * @return the timestamp when the message was created
     */
    public Instant getCreatedAt() {
        return createdAt;
    }
}
