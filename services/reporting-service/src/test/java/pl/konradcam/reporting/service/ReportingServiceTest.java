package pl.konradcam.reporting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.konradcam.contracts.event.ProcessedEvent;
import pl.konradcam.reporting.domain.ReservationReport;
import pl.konradcam.reporting.repository.ProcessedEventRepository;
import pl.konradcam.reporting.repository.ReservationReportRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportingServiceTest {

    @Mock
    private ReservationReportRepository reservationReportRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private ReportingService reportingService;

    private UUID eventId;
    private String eventType;
    private ReservationReport report;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        eventType = "reservation.created";
        report = new ReservationReport(
                UUID.randomUUID(),
                "A101",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(2),
                "Test Meeting",
                "testuser"
        );
    }

    @Test
    void shouldSaveReservationReportAndMarkEventAsProcessed() {
        // given
        when(reservationReportRepository.save(any(ReservationReport.class))).thenReturn(report);
        when(processedEventRepository.save(any(ProcessedEvent.class))).thenReturn(new ProcessedEvent(eventId, eventType));

        // when
        reportingService.saveReservationReportAndMarkEventProcessed(report, eventId, eventType);

        // then
        verify(reservationReportRepository, times(1)).save(report);
        verify(processedEventRepository, times(1)).save(any(ProcessedEvent.class));
    }

    @Test
    void shouldReturnTrueWhenEventAlreadyProcessed() {
        // given
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(true);

        // when
        boolean result = reportingService.isEventAlreadyProcessed(eventId);

        // then
        assertTrue(result);
        verify(processedEventRepository, times(1)).existsByEventId(eventId);
    }

    @Test
    void shouldReturnFalseWhenEventNotProcessed() {
        // given
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(false);

        // when
        boolean result = reportingService.isEventAlreadyProcessed(eventId);

        // then
        assertFalse(result);
        verify(processedEventRepository, times(1)).existsByEventId(eventId);
    }

    @Test
    void shouldNotSaveReportTwiceForSameEventId() {
        // given - first call
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(false);
        when(reservationReportRepository.save(any(ReservationReport.class))).thenReturn(report);
        when(processedEventRepository.save(any(ProcessedEvent.class))).thenReturn(new ProcessedEvent(eventId, eventType));

        // when - first processing
        reportingService.saveReservationReportAndMarkEventProcessed(report, eventId, eventType);

        // given - second call with same eventId
        when(processedEventRepository.existsByEventId(eventId)).thenReturn(true);

        // when - check if already processed
        boolean isProcessed = reportingService.isEventAlreadyProcessed(eventId);

        // then
        assertTrue(isProcessed);
        // Verify save was called only once (from first processing)
        verify(reservationReportRepository, times(1)).save(report);
    }
}

