package io.econexion.chat.service;

import io.econexion.chat.ChatConversation;
import io.econexion.chat.ChatMessage;
import io.econexion.chat.dto.MessageDTO;
import io.econexion.chat.repository.ChatConversationRepository;
import io.econexion.chat.repository.ChatMessageRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ChatServiceTest {

    private final ChatConversationRepository convRepo = mock(ChatConversationRepository.class);
    private final ChatMessageRepository msgRepo = mock(ChatMessageRepository.class);

    private final ChatService service = new ChatService(convRepo, msgRepo);

    // ==========================================================
    @Test
    void testSendMessage_conversationNotFound() {
        when(convRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                service.sendMessage(1L, 10L, "Hola")
        );
    }

    @Test
    void testSendMessage_senderNotInConversation() {
        ChatConversation c = new ChatConversation();
        c.setId(1L);
        c.setParticipant1Id(5L);
        c.setParticipant2Id(6L);

        when(convRepo.findById(1L)).thenReturn(Optional.of(c));

        assertThrows(IllegalArgumentException.class, () ->
                service.sendMessage(1L, 99L, "Hola")
        );
    }

    @Test
    void testGetOrCreateConversation_existing() {
        ChatConversation c = new ChatConversation();
        c.setId(1L);

        when(convRepo.findByOfferAndParticipantsAnyOrder(10L, 3L, 5L))
                .thenReturn(Optional.of(c));

        ChatConversation result = service.getOrCreateConversation(10L, 3L, 5L);

        assertEquals(1L, result.getId());
        verify(convRepo, times(0)).save(any());
    }

    @Test
    void testListMessages_emptyList() {
        when(msgRepo.findByConversation_IdOrderByCreatedAtAsc(1L))
                .thenReturn(List.of());

        List<MessageDTO> result = service.listMessages(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void testListConversations_previewEmpty() {
        ChatConversation c = new ChatConversation();
        c.setId(1L);
        c.setUpdatedAt(Instant.now());

        when(convRepo.findAllByUserOrdered(10L)).thenReturn(List.of(c));
        when(msgRepo.findByConversation_IdOrderByCreatedAtAsc(1L)).thenReturn(List.of());

        var result = service.listConversations(10L);

        assertEquals("", result.get(0).getPreview());
    }
}
