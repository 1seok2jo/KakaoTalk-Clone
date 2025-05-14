package oneseoktwojo.ohtalkhae.domain.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.notification.enums.NotificationType;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    private Long userId;
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;
    @Column(length = 200)
    private String message;
    @Column(length = 1000)
    private String clickTarget;

    private boolean isRead;
    private boolean isDeleted;
    private LocalDateTime createdAt;

}
