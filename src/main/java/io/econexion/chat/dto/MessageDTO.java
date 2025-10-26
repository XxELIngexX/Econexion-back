package io.econexion.chat.dto;

import java.time.Instant;

public class MessageDTO {
    private final Long id;
    private final Long senderId;
    private final String text;
    private final Instant createdAt;

    public MessageDTO(Long id, Long senderId, String text, Instant createdAt) {
        this.id = id; this.senderId = senderId; this.text = text; this.createdAt = createdAt;
    }
    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }
}