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

/**
 * REST controller that exposes endpoints for managing chat conversations and messages.
 * <p>
 * Base path: {@code /api/chat}
 * </p>
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    /**
     * Service responsible for chat-related business logic.
     */
    private final ChatService chatService;

    /**
     * Creates a new {@link ChatController} with the required {@link ChatService}.
     *
     * @param chatService service that handles chat operations
     */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Creates a new conversation or reuses an existing one for the given offer and participants.
     * Optionally sends the first message if it is provided and not blank.
     * <p>
     * Endpoint: {@code POST /api/chat/conversations}
     * </p>
     *
     * @param req request body containing offer id, sender id, receiver id, and optional first message
     * @return a {@link ResponseEntity} with a JSON map containing the {@code conversationId}
     */
    @PostMapping("/conversations")
    public ResponseEntity<?> createOrReuseConversation(@Valid @RequestBody CreateConversationRequest req) {
        ChatConversation c = chatService.getOrCreateConversation(
                req.getOfferId(),
                req.getSenderId(),
                req.getReceiverId()
        );
        if (req.getFirstMessage() != null && !req.getFirstMessage().isBlank()) {
            chatService.sendMessage(c.getId(), req.getSenderId(), req.getFirstMessage());
        }
        return ResponseEntity.ok(Map.of("conversationId", c.getId()));
    }

    /**
     * Lists all conversations for a specific user.
     * <p>
     * Endpoint: {@code GET /api/chat/conversations?userId={userId}}
     * </p>
     *
     * @param userId identifier of the user whose conversations will be retrieved
     * @return list of {@link ConversationSummaryDTO} objects representing the user's conversations
     */
    @GetMapping("/conversations")
    public List<ConversationSummaryDTO> listConversations(@RequestParam("userId") Long userId) {
        return chatService.listConversations(userId);
    }

    /**
     * Lists all messages belonging to a given conversation.
     * <p>
     * Endpoint: {@code GET /api/chat/conversations/{conversationId}/messages}
     * </p>
     *
     * @param conversationId identifier of the conversation
     * @return list of {@link MessageDTO} objects representing the messages in the conversation
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public List<MessageDTO> listMessages(@PathVariable Long conversationId) {
        return chatService.listMessages(conversationId);
    }

    /**
     * Sends a message in an existing conversation.
     * <p>
     * Endpoint: {@code POST /api/chat/conversations/{conversationId}/messages}
     * </p>
     *
     * @param conversationId identifier of the conversation where the message will be sent
     * @param req            request body containing the sender id and the message text
     * @return the created {@link MessageDTO} representing the persisted message
     */
    @PostMapping("/conversations/{conversationId}/messages")
    public MessageDTO sendMessage(@PathVariable Long conversationId,
                                  @Valid @RequestBody SendMessageRequest req) {
        return chatService.sendMessage(conversationId, req.getSenderId(), req.getText());
    }
}
