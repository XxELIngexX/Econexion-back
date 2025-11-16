package io.econexion.chat.controller;

import io.econexion.chat.ChatConversation;
import io.econexion.chat.dto.*;
import io.econexion.chat.service.ChatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@AutoConfigureMockMvc(addFilters = false)  // 🔥 FIX para remover seguridad en tests
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateConversation() throws Exception {

        ChatConversation c = new ChatConversation();
        c.setId(1L);
        c.setOfferId(10L);
        c.setParticipant1Id(100L);
        c.setParticipant2Id(200L);
        c.setUpdatedAt(Instant.now());

        CreateConversationRequest req = new CreateConversationRequest();
        req.setOfferId(10L);
        req.setSenderId(100L);
        req.setReceiverId(200L);
        req.setFirstMessage("Hola!!");

        Mockito.when(chatService.getOrCreateConversation(10L, 100L, 200L))
                .thenReturn(c);

        Mockito.when(chatService.sendMessage(1L, 100L, "Hola!!"))
                .thenReturn(new MessageDTO(1L, 100L, "Hola!!", Instant.now()));

        mockMvc.perform(post("/api/chat/conversations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value(1L));
    }

    @Test
    void testListConversations() throws Exception {

        ConversationSummaryDTO dto = new ConversationSummaryDTO(
                1L, 10L, 100L, 200L, Instant.now(), "Hola preview"
        );

        Mockito.when(chatService.listConversations(100L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/chat/conversations")
                .param("userId", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].conversationId").value(1L))
                .andExpect(jsonPath("$[0].preview").value("Hola preview"));
    }

    @Test
    void testListMessages() throws Exception {

        MessageDTO m = new MessageDTO(1L, 100L, "Hola!", Instant.now());

        Mockito.when(chatService.listMessages(1L))
                .thenReturn(List.of(m));

        mockMvc.perform(get("/api/chat/conversations/1/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].text").value("Hola!"))
                .andExpect(jsonPath("$[0].senderId").value(100L));
    }

    @Test
    void testSendMessage() throws Exception {

        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(100L);
        req.setText("Nuevo mensaje!");

        MessageDTO dto = new MessageDTO(55L, 100L, "Nuevo mensaje!", Instant.now());

        Mockito.when(chatService.sendMessage(1L, 100L, "Nuevo mensaje!"))
                .thenReturn(dto);

        mockMvc.perform(post("/api/chat/conversations/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(55L))
                .andExpect(jsonPath("$.text").value("Nuevo mensaje!"))
                .andExpect(jsonPath("$.senderId").value(100L));
    }
}
