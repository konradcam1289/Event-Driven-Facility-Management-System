package pl.konradcam.contracts.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity to track processed events for idempotency.
 * Prevents duplicate event processing in event-driven systems with at-least-once delivery.
 */
@Entity
@Table(
    name = "processed_events",
    indexes = {
        @Index(name = "idx_event_id", columnList = "eventId", unique = true),
        @Index(name = "idx_event_type", columnList = "eventType"),
        @Index(name = "idx_processed_at", columnList = "processedAt")
    }
)
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID eventId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private Instant processedAt;

    @Column(length = 500)
    private String notes;

    protected ProcessedEvent() {
    }

    public ProcessedEvent(UUID eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
    }

    public ProcessedEvent(UUID eventId, String eventType, String notes) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.notes = notes;
    }

    @PrePersist
    void onCreate() {
        this.processedAt = Instant.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

