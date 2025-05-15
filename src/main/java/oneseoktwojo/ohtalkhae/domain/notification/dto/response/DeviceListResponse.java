package oneseoktwojo.ohtalkhae.domain.notification.dto.response;

import lombok.Builder;
import lombok.Getter;
import oneseoktwojo.ohtalkhae.domain.notification.PushSubscription;

import java.time.LocalDateTime;

@Getter
public class DeviceListResponse {

    private Long subscriptionId;
    private String deviceName;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;

    @Builder
    public DeviceListResponse(Long subscriptionId, String deviceName, LocalDateTime createdAt, LocalDateTime lastUsedAt) {
        this.subscriptionId = subscriptionId;
        this.deviceName = deviceName;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
    }

    public static DeviceListResponse of(PushSubscription subscription) {
        return DeviceListResponse.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .deviceName(subscription.getDeviceName())
                .createdAt(subscription.getCreatedAt())
                .lastUsedAt(subscription.getLastUsedAt())
                .build();
    }
}
