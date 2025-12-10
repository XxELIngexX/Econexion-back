package io.econexion.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

/**
 * Entity that represents a conversation between users in the system.
 * <p>
 * A {@link Conversation} is:
 * <ul>
 *   <li>Linked to a single {@link Offer} (one-to-one relationship).</li>
 *   <li>Has a list of participating {@link User} entities.</li>
 *   <li>Contains a list of {@link Message} entities that belong to this conversation.</li>
 * </ul>
 * The entity is mapped to the {@code conversations} table.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "conversations")
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    /**
     * Primary key identifier of the conversation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private UUID id;

    /**
     * Offer associated with this conversation.
     * <p>
     * Each conversation is linked to exactly one offer.
     * </p>
     */
    @OneToOne
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    /**
     * List of users participating in this conversation.
     * <p>
     * Mapped through a join table {@code conversation_participants}.
     * </p>
     */
    @ManyToMany
    @JoinTable(
        name = "conversation_participants",
        joinColumns = @JoinColumn(name = "conversation_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();

    /**
     * Messages that belong to this conversation.
     * <p>
     * This is a one-to-many relationship; when the conversation is removed,
     * its messages are also removed due to {@code orphanRemoval = true}.
     * </p>
     */
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Message> messages = new ArrayList<>();
}
