package pl.konradcam.contracts.event;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface for ProcessedEvent.
 * Use @NoRepositoryBean to prevent Spring from creating bean instance of this interface.
 * Services should extend this interface.
 */
@NoRepositoryBean
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    /**
     * Find processed event by event ID.
     * Used for idempotency check.
     *
     * @param eventId the unique event ID
     * @return Optional containing the ProcessedEvent if found
     */
    Optional<ProcessedEvent> findByEventId(UUID eventId);

    /**
     * Check if event was already processed.
     * More efficient than findByEventId when you only need boolean result.
     *
     * @param eventId the unique event ID
     * @return true if event was processed, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(pe) > 0 THEN true ELSE false END FROM ProcessedEvent pe WHERE pe.eventId = :eventId")
    boolean existsByEventId(UUID eventId);
}

