package io.econexion.chat.repository;

import io.econexion.chat.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and managing {@link ChatConversation} entities.
 * <p>
 * Extends {@link JpaRepository} to provide basic CRUD operations and defines
 * custom query methods for retrieving conversations by offer and participants.
 * </p>
 */
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    /**
     * Finds a conversation by offer ID and the exact pair of participants in a fixed order.
     *
     * @param offerId the offer identifier associated with the conversation
     * @param p1      the first participant's user ID
     * @param p2      the second participant's user ID
     * @return an {@link Optional} containing the matching {@link ChatConversation},
     *         or empty if none is found
     */
    Optional<ChatConversation> findByOfferIdAndParticipant1IdAndParticipant2Id(Long offerId, Long p1, Long p2);

    /**
     * Retrieves all conversations where the given user is a participant
     * (either as participant1 or participant2), ordered by the last update time
     * in descending order (most recently updated first).
     *
     * @param userId the user ID of the participant
     * @return a list of {@link ChatConversation} instances ordered by {@code updatedAt} descending
     */
    @Query("select c from ChatConversation c " +
           "where c.participant1Id = :userId or c.participant2Id = :userId " +
           "order by c.updatedAt desc")
    List<ChatConversation> findAllByUserOrdered(Long userId);

    /**
     * Finds a conversation by offer ID and the pair of participants, regardless of their order.
     * <p>
     * This method checks both (a, b) and (b, a) combinations of participants for the same offer.
     * </p>
     *
     * @param offerId the offer identifier associated with the conversation
     * @param a       the first participant's user ID
     * @param b       the second participant's user ID
     * @return an {@link Optional} containing the matching {@link ChatConversation},
     *         or empty if none is found
     */
    @Query("select c from ChatConversation c " +
           "where c.offerId = :offerId and " +
           "((c.participant1Id = :a and c.participant2Id = :b) or (c.participant1Id = :b and c.participant2Id = :a))")
    Optional<ChatConversation> findByOfferAndParticipantsAnyOrder(Long offerId, Long a, Long b);
}
