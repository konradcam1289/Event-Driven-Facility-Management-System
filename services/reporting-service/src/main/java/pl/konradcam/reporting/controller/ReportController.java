package pl.konradcam.reporting.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.konradcam.reporting.domain.ReservationReport;
import pl.konradcam.reporting.repository.ReservationReportRepository;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReservationReportRepository reportRepository;

    public ReportController(ReservationReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping("/reservations")
    public List<ReservationReport> getReservationReports() {
        return reportRepository.findAll();
    }
}

