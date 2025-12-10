package io.econexion.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing a chat message between two users.
 * <p>
 * This DTO is typically used for transporting chat message information
 * through the application layers or over the network (e.g. WebSocket or REST).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    /**
     * Identifier of the conversation to which this message belongs.
     */
    private String conversationId;

    /**
     * Email address of the user who sends the message.
     */
    private String senderEmail;

    /**
     * Email address of the user who receives the message.
     */
    private String receiverEmail;

    /**
     * Text content of the chat message.
     */
    private String content;
}
