package pl.konradcam.notification.service;

import org.springframework.stereotype.Service;
import pl.konradcam.notification.domain.Notification;
import pl.konradcam.notification.domain.ProcessedEvent;
import pl.konradcam.notification.repository.NotificationRepository;
import pl.konradcam.notification.repository.ProcessedEventRepository;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ProcessedEventRepository processedEventRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            ProcessedEventRepository processedEventRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.processedEventRepository = processedEventRepository;
    }

    public void saveNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public void markEventProcessed(java.util.UUID eventId, String eventType) {
        ProcessedEvent processedEvent = new ProcessedEvent(eventId, eventType);
        processedEventRepository.save(processedEvent);
    }

    public boolean isEventAlreadyProcessed(java.util.UUID eventId) {
        return processedEventRepository.findByEventId(eventId).isPresent();
    }
}

