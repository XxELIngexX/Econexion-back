package io.econexion.chat.repository;

import io.econexion.chat.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for accessing and managing {@link ChatMessage} entities.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD operations and defines
 * a custom method for retrieving messages by conversation, ordered by
 * creation time.
 * </p>
 */
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Finds all messages that belong to the given conversation, ordered by
     * their creation time in ascending order (oldest first).
     *
     * @param conversationId the identifier of the conversation
     * @return a list of {@link ChatMessage} entities ordered by {@code createdAt} ascending
     */
    List<ChatMessage> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);
}
