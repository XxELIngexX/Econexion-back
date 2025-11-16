package io.econexion.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {
    private String conversationId;
    private String senderEmail;
    private String receiverEmail;
    private String content;
}
