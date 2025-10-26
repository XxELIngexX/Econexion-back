package io.econexion.chat.dto;

import jakarta.validation.constraints.NotNull;

public class CreateConversationRequest {
    @NotNull private Long offerId;
    @NotNull private Long senderId;
    @NotNull private Long receiverId;
    private String firstMessage;

    public Long getOfferId() { return offerId; }
    public void setOfferId(Long offerId) { this.offerId = offerId; }
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getFirstMessage() { return firstMessage; }
    public void setFirstMessage(String firstMessage) { this.firstMessage = firstMessage; }
}