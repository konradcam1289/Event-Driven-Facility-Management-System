package pl.konradcam.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.konradcam.contracts.event.ProcessedEvent;
import pl.konradcam.notification.domain.Notification;
import pl.konradcam.notification.repository.NotificationRepository;
import pl.konradcam.notification.repository.ProcessedEventRepository;

import java.util.UUID;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final ProcessedEventRepository processedEventRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            ProcessedEventRepository processedEventRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.processedEventRepository = processedEventRepository;
    }

    /**
     * Saves notification and marks event as processed in a single transaction.
     * This ensures atomicity - either both succeed or both fail.
     */
    @Transactional
    public void saveNotificationAndMarkEventProcessed(
            Notification notification,
            UUID eventId,
            String eventType
    ) {
        notificationRepository.save(notification);

        ProcessedEvent processedEvent = new ProcessedEvent(eventId, eventType);
        processedEventRepository.save(processedEvent);

        logger.debug("Saved notification and marked event {} as processed", eventId);
    }

    public boolean isEventAlreadyProcessed(UUID eventId) {
        return processedEventRepository.existsByEventId(eventId);
    }
}

