package io.econexion.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

/**
 * DTO que resume una conversación de chat para listar en la interfaz.
 * Ahora compatible con Lombok, con constructor vacío y setters.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSummaryDTO {

    private Long conversationId;
    private Long offerId;
    private Long participant1Id;
    private Long participant2Id;
    private Instant updatedAt;
    private String lastMessagePreview;

    /**
     * Alias usado por compatibilidad con algunas pruebas.
     */
    public String getPreview() {
        return lastMessagePreview;
    }
}
