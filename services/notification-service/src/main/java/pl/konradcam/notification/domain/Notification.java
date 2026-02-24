package pl.konradcam.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID reservationId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(length = 1000)
    private String message;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant sentAt;

    protected Notification() {
    }

    public Notification(UUID reservationId, String recipientEmail, String message) {
        this.reservationId = reservationId;
        this.recipientEmail = recipientEmail;
        this.message = message;
        this.status = NotificationStatus.PENDING;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public NotificationStatus getStatus() {
        return status;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    // Setters
    public void setStatus(NotificationStatus status) {
        this.status = status;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }
}

