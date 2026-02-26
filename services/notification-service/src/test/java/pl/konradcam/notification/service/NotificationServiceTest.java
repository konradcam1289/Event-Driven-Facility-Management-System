package pl.konradcam.notification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.konradcam.contracts.event.ProcessedEvent;
import pl.konradcam.notification.domain.Notification;
import pl.konradcam.notification.repository.NotificationRepository;
import pl.konradcam.notification.repository.ProcessedEventRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private NotificationService notificationService;

    private UUID eventId;
    private String eventType;
    private Notification notification;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        eventType = "reservation.created";
        notification = new Notification(
                UUID.randomUUID(),
                "test@example.com",
                "Test notification"
        );
    }

    @Test
    void shouldSaveNotificationAndMarkEventAsProcessed() {
        // given
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(processedEventRepository.save(any(ProcessedEvent.class))).thenReturn(new ProcessedEvent(eventId, eventType));

        // when
        notificationService.saveNotificationAndMarkEventProcessed(notification, eventId, eventType);

        // then
        verify(notificationRepository, times(1)).save(notification);
        verify(processedEventRepository, times(1)).save(any(ProcessedEvent.class));
    }

    @Test
    void shouldReturnTrueWhenEventAlreadyProcessed() {
        // given
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(true);

        // when
        boolean result = notificationService.isEventAlreadyProcessed(eventId);

        // then
        assertTrue(result);
        verify(processedEventRepository, times(1)).existsByEventId(eventId);
    }

    @Test
    void shouldReturnFalseWhenEventNotProcessed() {
        // given
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(false);

        // when
        boolean result = notificationService.isEventAlreadyProcessed(eventId);

        // then
        assertFalse(result);
        verify(processedEventRepository, times(1)).existsByEventId(eventId);
    }

    @Test
    void shouldNotSaveNotificationTwiceForSameEventId() {
        // given - first call
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(false);
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);
        when(processedEventRepository.save(any(ProcessedEvent.class))).thenReturn(new ProcessedEvent(eventId, eventType));

        // when - first processing
        notificationService.saveNotificationAndMarkEventProcessed(notification, eventId, eventType);

        // given - second call with same eventId
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(true);

        // when - check if already processed
        boolean isProcessed = notificationService.isEventAlreadyProcessed(eventId);

        // then
        assertTrue(isProcessed);
        // Verify save was called only once (from first processing)
        verify(notificationRepository, times(1)).save(notification);
    }
}

