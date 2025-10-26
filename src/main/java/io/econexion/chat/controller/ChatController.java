package io.econexion.chat.controller;

import io.econexion.chat.ChatConversation;
import io.econexion.chat.dto.CreateConversationRequest;
import io.econexion.chat.dto.SendMessageRequest;
import io.econexion.chat.dto.ConversationSummaryDTO;
import io.econexion.chat.dto.MessageDTO;
import io.econexion.chat.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/conversations")
    public ResponseEntity<?> createOrReuseConversation(@Valid @RequestBody CreateConversationRequest req) {
        ChatConversation c = chatService.getOrCreateConversation(req.getOfferId(), req.getSenderId(), req.getReceiverId());
        if (req.getFirstMessage() != null && !req.getFirstMessage().isBlank()) {
            chatService.sendMessage(c.getId(), req.getSenderId(), req.getFirstMessage());
        }
        return ResponseEntity.ok(Map.of("conversationId", c.getId()));
    }

    @GetMapping("/conversations")
    public List<ConversationSummaryDTO> listConversations(@RequestParam("userId") Long userId) {
        return chatService.listConversations(userId);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageDTO> listMessages(@PathVariable Long conversationId) {
        return chatService.listMessages(conversationId);
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public MessageDTO sendMessage(@PathVariable Long conversationId,
                                  @Valid @RequestBody SendMessageRequest req) {
        return chatService.sendMessage(conversationId, req.getSenderId(), req.getText());
    }
}