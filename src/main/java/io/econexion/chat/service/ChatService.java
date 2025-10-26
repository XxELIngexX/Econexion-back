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

@Service
public class ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;

    public ChatService(ChatConversationRepository conversationRepository,
                       ChatMessageRepository messageRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
    }

    @Transactional
    public ChatConversation getOrCreateConversation(Long offerId, Long senderId, Long receiverId) {
        return conversationRepository.findByOfferAndParticipantsAnyOrder(offerId, senderId, receiverId)
                .orElseGet(() -> {
                    ChatConversation c = new ChatConversation();
                    c.setOfferId(offerId);
                    Long p1 = senderId < receiverId ? senderId : receiverId;
                    Long p2 = senderId < receiverId ? receiverId : senderId;
                    c.setParticipant1Id(p1);
                    c.setParticipant2Id(p2);
                    return conversationRepository.save(c);
                });
    }

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

    @Transactional(readOnly = true)
    public List<MessageDTO> listMessages(Long conversationId) {
        return messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversationId).stream()
                .map(m -> new MessageDTO(m.getId(), m.getSenderId(), m.getText(), m.getCreatedAt()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConversationSummaryDTO> listConversations(Long userId) {
        List<ChatConversation> convs = conversationRepository.findAllByUserOrdered(userId);

        return convs.stream().map(c -> {
            String preview = messageRepository.findByConversation_IdOrderByCreatedAtAsc(c.getId()).stream()
                    .max(Comparator.comparing(ChatMessage::getCreatedAt))
                    .map(m -> {
                        String t = m.getText();
                        return t.length() > 80 ? t.substring(0, 80) + "…" : t;
                    })
                    .orElse("");
            return new ConversationSummaryDTO(
                    c.getId(), c.getOfferId(), c.getParticipant1Id(), c.getParticipant2Id(),
                    c.getUpdatedAt(), preview
            );
        }).toList();
    }
}