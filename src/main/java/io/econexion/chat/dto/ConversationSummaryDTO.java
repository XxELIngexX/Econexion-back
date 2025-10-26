package io.econexion.chat.dto;

import java.time.Instant;

public class ConversationSummaryDTO {
    private final Long conversationId;
    private final Long offerId;
    private final Long participant1Id;
    private final Long participant2Id;
    private final Instant updatedAt;
    private final String lastMessagePreview;

    public ConversationSummaryDTO(Long conversationId, Long offerId, Long participant1Id, Long participant2Id,
                                  Instant updatedAt, String lastMessagePreview) {
        this.conversationId = conversationId;
        this.offerId = offerId;
        this.participant1Id = participant1Id;
        this.participant2Id = participant2Id;
        this.updatedAt = updatedAt;
        this.lastMessagePreview = lastMessagePreview;
    }

    public Long getConversationId() { return conversationId; }
    public Long getOfferId() { return offerId; }
    public Long getParticipant1Id() { return participant1Id; }
    public Long getParticipant2Id() { return participant2Id; }
    public Instant getUpdatedAt() { return updatedAt; }
    public String getLastMessagePreview() { return lastMessagePreview; }
}