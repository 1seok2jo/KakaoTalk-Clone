package oneseoktwojo.ohtalkhae.domain.notification.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PushSubscribeRequest {

    private Long userId;
    private String endPoint;
    private String publicKey;
    private String auth;
    private String deviceName;
    private LocalDateTime createdAt;

    @Builder
    public PushSubscribeRequest(Long userId, String endPoint, String publicKey, String auth, String deviceName,
                                LocalDateTime createdAt) {
        this.userId = userId;
        this.endPoint = endPoint;
        this.publicKey = publicKey;
        this.auth = auth;
        this.deviceName = deviceName;
        this.createdAt = createdAt;
    }
}
