package io.econexion.chat.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request payload used to create or reuse a chat conversation.
 * <p>
 * This DTO includes the identifiers of the offer and both participants,
 * and optionally an initial message to be sent when the conversation
 * is created.
 * </p>
 */
public class CreateConversationRequest {

    /**
     * Identifier of the offer associated with the conversation.
     * This field is required.
     */
    @NotNull
    private Long offerId;

    /**
     * Identifier of the user who initiates the conversation
     * or sends the first message. This field is required.
     */
    @NotNull
    private Long senderId;

    /**
     * Identifier of the user who will receive the first message.
     * This field is required.
     */
    @NotNull
    private Long receiverId;

    /**
     * Optional first message to be sent when the conversation is created.
     * If {@code null} or blank, no initial message will be sent.
     */
    private String firstMessage;

    /**
     * Returns the offer identifier associated with this request.
     *
     * @return the offer id
     */
    public Long getOfferId() {
        return offerId;
    }

    /**
     * Sets the offer identifier associated with this request.
     *
     * @param offerId the offer id to set
     */
    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    /**
     * Returns the sender's user identifier.
     *
     * @return the sender id
     */
    public Long getSenderId() {
        return senderId;
    }

    /**
     * Sets the sender's user identifier.
     *
     * @param senderId the sender id to set
     */
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    /**
     * Returns the receiver's user identifier.
     *
     * @return the receiver id
     */
    public Long getReceiverId() {
        return receiverId;
    }

    /**
     * Sets the receiver's user identifier.
     *
     * @param receiverId the receiver id to set
     */
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * Returns the optional first message to be sent in the conversation.
     *
     * @return the first message text, or {@code null} if none
     */
    public String getFirstMessage() {
        return firstMessage;
    }

    /**
     * Sets the optional first message to be sent in the conversation.
     *
     * @param firstMessage the first message text to set
     */
    public void setFirstMessage(String firstMessage) {
        this.firstMessage = firstMessage;
    }
}
