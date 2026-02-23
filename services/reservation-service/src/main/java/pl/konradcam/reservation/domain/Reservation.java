package pl.konradcam.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String createdBy;

    @Column(nullable = false)
    private Instant createdAt;

    protected Reservation() {
    }

    public Reservation(
            String roomId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String title,
            String createdBy
    ) {
        this.roomId = roomId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.title = title;
        this.createdBy = createdBy;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getRoomId() {
        return roomId;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public LocalDateTime getEndAt() {
        return endAt;
    }

    public String getTitle() {
        return title;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

