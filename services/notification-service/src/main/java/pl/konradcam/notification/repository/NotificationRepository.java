package pl.konradcam.notification.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.konradcam.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
}

