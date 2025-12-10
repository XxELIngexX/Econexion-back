package io.econexion.chat.service;

import io.econexion.chat.ChatConversation;
import io.econexion.chat.ChatMessage;
import io.econexion.chat.dto.ConversationSummaryDTO;
import io.econexion.chat.dto.MessageDTO;
import io.econexion.chat.repository.ChatConversationRepository;
import io.econexion.chat.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * Service layer responsible for managing chat conversations and messages.
 * <p>
 * Provides operations to create or reuse conversations, send messages,
 * list messages, and list conversations with previews. Ensures proper
 * transactional boundaries for consistency.
 * </p>
 */
@Service
public class ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;

    /**
     * Constructs a new {@link ChatService} with the required repositories.
     *
     * @param conversationRepository repository for chat conversations
     * @param messageRepository       repository for chat messages
     */
    public ChatService(ChatConversationRepository conversationRepository,
                       ChatMessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    /**
     * Retrieves an existing conversation for a given offer and participants,
     * or creates a new one if none exists. Participant ordering is normalized
     * to ensure consistent conversation identity.
     *
     * @param offerId    ID of the related offer
     * @param senderId   ID of the user initiating the request
     * @param receiverId ID of the other participant
     * @return the retrieved or newly created {@link ChatConversation}
     */
    @Transactional
    public ChatConversation getOrCreateConversation(Long offerId, Long senderId, Long receiverId) {
        return conversationRepository.findByOfferAndParticipantsAnyOrder(offerId, senderId, receiverId)
                .orElseGet(() -> {
                    ChatConversation c = new ChatConversation();
                    c.setOfferId(offerId);

                    // Normalize participant order
                    Long p1 = Math.min(senderId, receiverId);
                    Long p2 = Math.max(senderId, receiverId);

                    c.setParticipant1Id(p1);
                    c.setParticipant2Id(p2);
                    return conversationRepository.save(c);
                });
    }

    /**
     * Sends a message within a specified conversation.
     * <p>
     * Validates that the sender belongs to the conversation,
     * persists the message, updates the conversation timestamp,
     * and returns a DTO representation.
     * </p>
     *
     * @param conversationId ID of the conversation where the message is sent
     * @param senderId       ID of the sender
     * @param text           message content
     * @return a {@link MessageDTO} representing the saved message
     */
    @Transactional
    public MessageDTO sendMessage(Long conversationId, Long senderId, String text) {
        ChatConversation c = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        if (!c.involvesUser(senderId)) {
            throw new IllegalArgumentException("Sender not in conversation");
        }

        ChatMessage m = new ChatMessage();
        m.setConversation(c);
        m.setSenderId(senderId);
        m.setText(text);
        m = messageRepository.save(m);

        c.setUpdatedAt(Instant.now());
        conversationRepository.save(c);

        return new MessageDTO(m.getId(), m.getSenderId(), m.getText(), m.getCreatedAt());
    }

    /**
     * Returns all messages for a given conversation, ordered
     * chronologically from oldest to newest.
     *
     * @param conversationId ID of the conversation
     * @return list of {@link MessageDTO}
     */
    @Transactional(readOnly = true)
    public List<MessageDTO> listMessages(Long conversationId) {
        return messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversationId).stream()
                .map(m -> new MessageDTO(m.getId(), m.getSenderId(), m.getText(), m.getCreatedAt()))
                .toList();
    }

    /**
     * Returns all conversations for a given user, ordered by most recent updates.
     * Each conversation includes a preview of the latest message (up to 80 chars).
     *
     * @param userId ID of the user whose conversations should be listed
     * @return list of {@link ConversationSummaryDTO} objects
     */
    @Transactional(readOnly = true)
    public List<ConversationSummaryDTO> listConversations(Long userId) {
        List<ChatConversation> convs = conversationRepository.findAllByUserOrdered(userId);

        return convs.stream().map(c -> {

            // Extract preview of latest message
            String preview = messageRepository.findByConversation_IdOrderByCreatedAtAsc(c.getId()).stream()
                    .max(Comparator.comparing(ChatMessage::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(m -> {
                        String t = m.getText();
                        return t.length() > 80 ? t.substring(0, 80) + "…" : t;
                    })
                    .orElse("");

            return new ConversationSummaryDTO(
                    c.getId(),
                    c.getOfferId(),
                    c.getParticipant1Id(),
                    c.getParticipant2Id(),
                    c.getUpdatedAt(),
                    preview
            );

        }).toList();
    }
}
