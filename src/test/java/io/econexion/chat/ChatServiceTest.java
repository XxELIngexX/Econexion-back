package io.econexion.chat;

import io.econexion.chat.dto.ConversationSummaryDTO;
import io.econexion.chat.dto.MessageDTO;
import io.econexion.chat.repository.ChatConversationRepository;
import io.econexion.chat.repository.ChatMessageRepository;
import io.econexion.chat.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    ChatConversationRepository convRepo;

    @Mock
    ChatMessageRepository msgRepo;

    @InjectMocks
    ChatService chatService;

    ChatConversation c1, c2;

    @BeforeEach
    void init() {
        c1 = new ChatConversation();
        c1.setId(1L);
        c1.setOfferId(1L);
        c1.setParticipant1Id(10L);
        c1.setParticipant2Id(20L);
        c1.setUpdatedAt(Instant.now().minusSeconds(10));

        c2 = new ChatConversation();
        c2.setId(2L);
        c2.setOfferId(2L);
        c2.setParticipant1Id(10L);
        c2.setParticipant2Id(30L);
        c2.setUpdatedAt(Instant.now());
    }

    @Test
    void listConversations_ordersAndBuildsPreview() {
        when(convRepo.findAllByUserOrdered(10L)).thenReturn(List.of(c2, c1));

        ChatMessage m1 = new ChatMessage();
        m1.setText("Hola");
        m1.setSenderId(10L);
        m1.setCreatedAt(Instant.now().minusSeconds(5));

        ChatMessage m2 = new ChatMessage();
        m2.setText("Mensaje largo ".repeat(20));
        m2.setSenderId(20L);
        m2.setCreatedAt(Instant.now());

        when(msgRepo.findByConversation_IdOrderByCreatedAtAsc(any()))
                .thenReturn(List.of(m1, m2));

        List<ConversationSummaryDTO> list = chatService.listConversations(10L);

        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.get(0).getPreview().length() <= 81);
    }

    @Test
    void sendMessage_savesAndReturnsDTO() {
        ChatConversation mockConv = new ChatConversation();
        mockConv.setId(123L);
        mockConv.setParticipant1Id(10L);
        mockConv.setParticipant2Id(20L);

        when(convRepo.findById(123L)).thenReturn(Optional.of(mockConv));

        when(msgRepo.save(any(ChatMessage.class))).thenAnswer(inv -> {
            ChatMessage saved = inv.getArgument(0);
            saved.setCreatedAt(Instant.now());
            return saved;
        });

        MessageDTO dto = chatService.sendMessage(123L, 10L, "hola");
        assertNotNull(dto);
        assertEquals("hola", dto.getText());
        assertEquals(10L, dto.getSenderId());
        verify(msgRepo).save(any(ChatMessage.class));
    }

    @Test
    void listMessages_delegatesToRepo() {
        when(msgRepo.findByConversation_IdOrderByCreatedAtAsc(99L)).thenReturn(List.of());
        List<?> out = chatService.listMessages(99L);
        assertNotNull(out);
        verify(msgRepo).findByConversation_IdOrderByCreatedAtAsc(99L);
    }
}
