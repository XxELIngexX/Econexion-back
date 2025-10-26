package io.econexion.chat.repository;

import io.econexion.chat.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    Optional<ChatConversation> findByOfferIdAndParticipant1IdAndParticipant2Id(Long offerId, Long p1, Long p2);

    @Query("select c from ChatConversation c " +
           "where c.participant1Id = :userId or c.participant2Id = :userId " +
           "order by c.updatedAt desc")
    List<ChatConversation> findAllByUserOrdered(Long userId);

    @Query("select c from ChatConversation c " +
           "where c.offerId = :offerId and " +
           "((c.participant1Id = :a and c.participant2Id = :b) or (c.participant1Id = :b and c.participant2Id = :a))")
    Optional<ChatConversation> findByOfferAndParticipantsAnyOrder(Long offerId, Long a, Long b);
}