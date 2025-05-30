package MathCaptain.weakness.domain.Notification.repository;

import MathCaptain.weakness.domain.Notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findById(Long id);

    List<Notification> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
