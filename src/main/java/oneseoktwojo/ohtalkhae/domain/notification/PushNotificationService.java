package oneseoktwojo.ohtalkhae.domain.notification;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import oneseoktwojo.ohtalkhae.domain.notification.dto.request.PushSubscribeRequest;
import oneseoktwojo.ohtalkhae.domain.notification.dto.WebPushMessage;
import oneseoktwojo.ohtalkhae.domain.notification.dto.response.DeviceListResponse;
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
public class PushNotificationService {

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

    @Transactional
    public void sendPushTo(Long userId, WebPushMessage message, LocalDateTime now) {

        List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserId(userId);
        if (subscriptions.isEmpty())
            return;

        sendNotifications(subscriptions, message, now);
    }

    @Transactional
    public void sendPushTo(List<Long> userIds, WebPushMessage message, LocalDateTime now) {

        List<PushSubscription> subscriptions = pushSubscriptionRepository.findByUserIdIn(userIds);
        if (subscriptions.isEmpty())
            return;

        sendNotifications(subscriptions, message, now);
    }

    private void sendNotifications(List<PushSubscription> subscriptions, WebPushMessage message, LocalDateTime now) {
        for (PushSubscription subscription : subscriptions) {
            try {
                Notification notification = new Notification(
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
}
