package oneseoktwojo.ohtalkhae.domain.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.PushService;
import oneseoktwojo.ohtalkhae.domain.notification.dto.request.PushSubscribeRequest;
import oneseoktwojo.ohtalkhae.domain.notification.dto.WebPushMessage;
import oneseoktwojo.ohtalkhae.domain.notification.dto.response.DeviceListResponse;
import oneseoktwojo.ohtalkhae.domain.notification.enums.NotificationType;
import oneseoktwojo.ohtalkhae.domain.notification.mapper.PushSubscriptionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final PushSubscriptionMapper pushSubscriptionMapper;
    private final PushService pushService;
    private final ObjectMapper objectMapper;

    @Transactional
    public PushSubscription subscribe(PushSubscribeRequest request) {
        PushSubscription subscription = pushSubscriptionMapper.toPushSubscription(request);
        return pushSubscriptionRepository.save(subscription);
    }

    @Transactional
    public void unsubscribe(PushSubscribeRequest request) {
        Optional<PushSubscription> subscriptionOptional =
                pushSubscriptionRepository.findByEndPoint(request.getEndPoint());
        subscriptionOptional.ifPresent(pushSubscriptionRepository::delete);
    }

    public List<DeviceListResponse> getSubscribedDevices(Long userId) {
        List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserId(userId);

        return subscriptions.stream()
                .map(s -> DeviceListResponse.of(s))
                .toList();
    }

    /**
     * 단일 회원을 대상으로 푸시 알림을 보냅니다.
     * @param userId 푸시 알림을 보낼 회원 userId
     * @param message 웹 푸시 메시지 객체
     * @param now 현재 일시
     */
    @Transactional
    public void sendPushTo(Long userId, WebPushMessage message, LocalDateTime now) {

        List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserId(userId);
        if (!subscriptions.isEmpty()) {
            sendNotifications(subscriptions, message, now);
        }

        logNotification(
                Notification.builder()
                        .userId(userId)
                        .type(NotificationType.NEW_MESSAGE)
                        .title(message.getTitle())
                        .message(message.getBody())
                        .clickTarget(message.getClickTarget())
                        .isRead(false)
                        .isDeleted(false)
                        .createdAt(now)
                        .build()
        );
    }

    /**
     * 다수 회원을 대상으로 푸시 알림을 보냅니다.
     * @param userIds 푸시 알림을 보낼 회원 userId 리스트
     * @param message 웹 푸시 메시지 객체
     * @param now 현재 일시
     */
    @Transactional
    public void sendPushTo(List<Long> userIds, WebPushMessage message, LocalDateTime now) {

        List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserIdIn(userIds);
        if (!subscriptions.isEmpty()) {
            sendNotifications(subscriptions, message, now);
        }

        for (Long userId : userIds) {
            logNotification(
                    Notification.builder()
                            .userId(userId)
                            .type(NotificationType.NEW_MESSAGE)
                            .title(message.getTitle())
                            .message(message.getBody())
                            .clickTarget(message.getClickTarget())
                            .isRead(false)
                            .isDeleted(false)
                            .createdAt(now)
                            .build()
            );
        }
    }

    private void sendNotifications(List<PushSubscription> subscriptions, WebPushMessage message, LocalDateTime now) {
        for (PushSubscription subscription : subscriptions) {
            try {
                nl.martijndwars.webpush.Notification notification = new nl.martijndwars.webpush.Notification(
                        subscription.getEndPoint(),
                        subscription.getPublicKey(),
                        subscription.getAuth(),
                        objectMapper.writeValueAsBytes(message)
                );

                pushService.send(notification);

                subscription.setLastUsedAt(now);
                pushSubscriptionRepository.save(subscription);
            }
            catch (Exception e) {
                log.info("Failed to send push notification to user {}.", subscription.getUserId(), e);
            }
        }
    }

    private void logNotification(Notification notification) {
        Notification previousLog = notificationRepository.findByUserIdAndTitle(
                notification.getUserId(),
                notification.getTitle()
        );

        if (previousLog != null) {
            // 이전 동일한 제목의 알림이 있었으면 최신 메시지로 갱신
            notification = previousLog.toBuilder()
                    .type(notification.getType())
                    .message(notification.getMessage())
                    .clickTarget(notification.getClickTarget())
                    .isRead(false)
                    .isDeleted(false)
                    .createdAt(notification.getCreatedAt())
                    .build();
        }

        notificationRepository.save(notification);
    }
}
