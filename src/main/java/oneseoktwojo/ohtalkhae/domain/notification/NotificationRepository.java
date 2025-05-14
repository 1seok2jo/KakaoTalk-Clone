package oneseoktwojo.ohtalkhae.domain.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Notification findByUserIdAndTitle(Long userId, String title);

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
}
