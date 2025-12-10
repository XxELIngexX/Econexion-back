package io.econexion.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload used to send a new message in an existing conversation.
 * <p>
 * This DTO contains the sender's identifier and the message text. Validation
 * annotations ensure that both fields are provided and valid.
 * </p>
 */
public class SendMessageRequest {

    /**
     * Identifier of the user sending the message.
     * This field is required.
     */
    @NotNull
    private Long senderId;

    /**
     * Text content of the message being sent.
     * Must not be null or blank.
     */
    @NotBlank
    private String text;

    /**
     * Returns the sender's identifier.
     *
     * @return the sender ID
     */
    public Long getSenderId() {
        return senderId;
    }

    /**
     * Sets the sender's identifier.
     *
     * @param senderId the sender ID to set
     */
    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    /**
     * Returns the text of the message to be sent.
     *
     * @return the message text
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text of the message to be sent.
     *
     * @param text the message text
     */
    public void setText(String text) {
        this.text = text;
    }
}
