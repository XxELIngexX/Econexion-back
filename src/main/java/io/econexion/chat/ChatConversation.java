package io.econexion.chat;

import jakarta.persistence.*;
import java.time.Instant;

@Entity(name = "ChatConversation")
@Table(name = "chat_conversations",
       uniqueConstraints = @UniqueConstraint(columnNames = {"offer_id","participant1_id","participant2_id"}),
       indexes = {
         @Index(name="idx_chat_conv_offer", columnList="offer_id"),
         @Index(name="idx_chat_conv_updated", columnList="updated_at")
       })
public class ChatConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="offer_id", nullable=false)
    private Long offerId;

    @Column(name="participant1_id", nullable=false)
    private Long participant1Id;

    @Column(name="participant2_id", nullable=false)
    private Long participant2Id;

    @Column(name="created_at", nullable=false, updatable=false)
    private Instant createdAt;

    @Column(name="updated_at", nullable=false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate
    protected void onUpdate() { this.updatedAt = Instant.now(); }

    public boolean involvesUser(Long userId) {
        return userId != null && (userId.equals(participant1Id) || userId.equals(participant2Id));
    }

    // getters/setters
    public Long getId() { return id; }
    public Long getOfferId() { return offerId; }
    public void setOfferId(Long offerId) { this.offerId = offerId; }
    public Long getParticipant1Id() { return participant1Id; }
    public void setParticipant1Id(Long participant1Id) { this.participant1Id = participant1Id; }
    public Long getParticipant2Id() { return participant2Id; }
    public void setParticipant2Id(Long participant2Id) { this.participant2Id = participant2Id; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}