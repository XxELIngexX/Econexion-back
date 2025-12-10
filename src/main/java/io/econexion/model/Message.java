package io.econexion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity that represents a single message sent within a {@link Conversation}.
 * <p>
 * Each message has:
 * <ul>
 *   <li>A unique UUID identifier</li>
 *   <li>Text content</li>
 *   <li>A {@link User} as the sender</li>
 *   <li>A reference to the {@link Conversation} it belongs to</li>
 *   <li>A creation timestamp</li>
 * </ul>
 * This entity is mapped to the {@code messages} table.
 * </p>
 */
@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    /**
     * Primary key identifier for the message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Textual content of the message.
     */
    @Column(name = "text", columnDefinition = "TEXT", nullable = false)
    private String text;

    /**
     * User who sent this message.
     */
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * Conversation to which this message belongs.
     */
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    /**
     * Date and time when the message was created.
     * <p>
     * Initialized to the current time by default and marked as not updatable.
     * </p>
     */
    @Column(name = "date", nullable = false, updatable = false)
    private LocalDateTime date = LocalDateTime.now();
}
