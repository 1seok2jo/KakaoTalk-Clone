package oneseoktwojo.ohtalkhae.domain.notification.mapper;

import oneseoktwojo.ohtalkhae.domain.notification.PushSubscription;
import oneseoktwojo.ohtalkhae.domain.notification.dto.request.PushSubscribeRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring"
)
public interface PushSubscriptionMapper {
    PushSubscriptionMapper INSTANCE = Mappers.getMapper(PushSubscriptionMapper.class);

    PushSubscription toPushSubscription(PushSubscribeRequest request);
}
