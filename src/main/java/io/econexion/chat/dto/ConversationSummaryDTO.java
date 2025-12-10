package io.econexion.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * Data Transfer Object that summarizes a chat conversation for listing purposes
 * (e.g. showing a conversation list in the UI).
 * <p>
 * This DTO is compatible with Lombok annotations, providing automatic
 * getters, setters, constructors, and other utility methods.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSummaryDTO {

    /**
     * Unique identifier of the conversation.
     */
    private Long conversationId;

    /**
     * Identifier of the offer associated with the conversation.
     */
    private Long offerId;

    /**
     * Identifier of the first participant.
     */
    private Long participant1Id;

    /**
     * Identifier of the second participant.
     */
    private Long participant2Id;

    /**
     * Timestamp representing when the conversation was last updated,
     * typically the time of the most recent message.
     */
    private Instant updatedAt;

    /**
     * Preview text of the last message in the conversation.
     * Used for displaying a short summary in conversation lists.
     */
    private String lastMessagePreview;

    /**
     * Legacy alias kept for compatibility with certain tests or clients.
     *
     * @return the last message preview string
     */
    public String getPreview() {
        return lastMessagePreview;
    }
}
