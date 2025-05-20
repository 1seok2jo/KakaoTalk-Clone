package oneseoktwojo.ohtalkhae.domain.notification.mapper;

import oneseoktwojo.ohtalkhae.domain.notification.Notification;
import oneseoktwojo.ohtalkhae.domain.notification.dto.response.NotificationListResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring"
)
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    NotificationListResponse toNotificationListResponse(Notification notification);
}
