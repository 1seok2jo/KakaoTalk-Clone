package oneseoktwojo.ohtalkhae.domain.notification;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class PushSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;
    private Long userId;
    @Column(length = 1000)
    private String endPoint;
    @Column(length = 500)
    private String publicKey;
    @Column(length = 500)
    private String auth;
    private String deviceName;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;

    @Builder
    public PushSubscription(Long subscriptionId, Long userId, String endPoint, String publicKey, String auth,
                            String deviceName, LocalDateTime createdAt, LocalDateTime lastUsedAt) {
        this.subscriptionId = subscriptionId;
        this.userId = userId;
        this.endPoint = endPoint;
        this.publicKey = publicKey;
        this.auth = auth;
        this.deviceName = deviceName;
        this.createdAt = createdAt;
        this.lastUsedAt = lastUsedAt;
    }
}
