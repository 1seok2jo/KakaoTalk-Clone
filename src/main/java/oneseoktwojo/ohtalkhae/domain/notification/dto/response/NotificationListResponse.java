package oneseoktwojo.ohtalkhae.domain.notification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import oneseoktwojo.ohtalkhae.domain.notification.enums.NotificationType;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class NotificationListResponse {

    private Long notificationId;
    private NotificationType type;
    private String title;
    private String message;
    private String clickTarget;
    private boolean isRead;
    private boolean isDeleted;
    private LocalDateTime createdAt;
}
