package io.econexion.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.econexion.chat.dto.SendMessageRequest;
import io.econexion.chat.service.ChatService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)  // <<< DESACTIVA LA SEGURIDAD
public class ChatControllerErrorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSendMessage_validationError() throws Exception {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(10L);
        req.setText(null); // inválido

        mockMvc.perform(post("/api/chat/conversations/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSendMessage_serviceThrows() throws Exception {
        SendMessageRequest req = new SendMessageRequest();
        req.setSenderId(10L);
        req.setText("Hola!");

        Mockito.when(chatService.sendMessage(anyLong(), anyLong(), any()))
                .thenThrow(new IllegalArgumentException("Error"));

        mockMvc.perform(post("/api/chat/conversations/1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().is5xxServerError());
    }
}
