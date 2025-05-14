package oneseoktwojo.ohtalkhae.domain.notification;

import nl.martijndwars.webpush.PushService;
import oneseoktwojo.ohtalkhae.IntegrationTestSupport;
import oneseoktwojo.ohtalkhae.domain.notification.dto.WebPushMessage;
import oneseoktwojo.ohtalkhae.domain.notification.dto.request.PushSubscribeRequest;
import oneseoktwojo.ohtalkhae.domain.notification.dto.response.DeviceListResponse;
import oneseoktwojo.ohtalkhae.domain.notification.dto.response.NotificationListResponse;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

class PushNotificationServiceTest extends IntegrationTestSupport {

    @Autowired
    NotificationService notificationService;
    @Autowired
    NotificationRepository notificationRepository;
    @Autowired
    PushSubscriptionRepository pushSubscriptionRepository;

    @AfterEach
    void tearDown() {
        notificationRepository.deleteAllInBatch();
        pushSubscriptionRepository.deleteAllInBatch();
    }

    @DisplayName("웹 푸시 구독 요청을 저장합니다.")
    @Test
    void subscribe() {
        // given
        Long userId = 1L;
        String endPoint = "https://fcm.googleapis.com/fcm/send/cx1Ykq8bSxE:APA91bF3YX98k0p-EXAMPLE_END_POINT";
        PushSubscribeRequest request = createPushSubscribeRequest(userId, endPoint);

        // when
        notificationService.subscribe(request);

        // then
        PushSubscription found = pushSubscriptionRepository.findByEndPoint(endPoint).orElseThrow();
        assertThat(found)
                .extracting("userId", "endPoint")
                .containsExactly(userId, endPoint);
    }

    @DisplayName("웹 푸시 구독 정보를 삭제합니다.")
    @Test
    void unsubscribe() {
        // given
        Long userId = 1L;
        String endPoint = "https://fcm.googleapis.com/fcm/send/cx1Ykq8bSxE:APA91bF3YX98k0p-EXAMPLE_END_POINT";
        PushSubscribeRequest request = createPushSubscribeRequest(userId, endPoint);
        PushSubscription saved = notificationService.subscribe(request);

        // when
        notificationService.unsubscribe(request);

        // then
        Optional<PushSubscription> opSubscription = pushSubscriptionRepository.findById(saved.getSubscriptionId());
        assertThat(opSubscription.isEmpty());
    }

    @DisplayName("존재하지 않는 웹 푸시 구독 정보를 삭제해도 예외를 던지지 않습니다.")
    @Test
    void unsubscribeNotExist() {
        // given
        Long userId = 1L;
        String endPoint = "https://fcm.googleapis.com/fcm/send/cx1Ykq8bSxE:APA91bF3YX98k0p-EXAMPLE_END_POINT";
        PushSubscribeRequest request = createPushSubscribeRequest(userId, endPoint);

        // when then
        notificationService.unsubscribe(request);
    }

    @DisplayName("구독 중인 장치 목록을 조회합니다.")
    @Test
    void getSubscribedDevices() {
        // given
        Long userId = 1L;
        String endPoint = "https://fcm.googleapis.com/fcm/send/cx1Ykq8bSxE:APA91bF3YX98k0p-EXAMPLE_END_POINT";
        PushSubscribeRequest request = createPushSubscribeRequest(userId, endPoint);
        PushSubscription saved = notificationService.subscribe(request);

        // when
        List<DeviceListResponse> devices = notificationService.getSubscribedDevices(userId);

        // then
        assertThat(devices).hasSize(1);
        assertThat(devices.get(0).getSubscriptionId()).isEqualTo(saved.getSubscriptionId());
    }

    @DisplayName("푸시 알림을 보내면 알림 목록이 저장됩니다.")
    @Test
    void sendPushTo() throws Exception {
        // given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.now();
        String title = "채팅방 이름";
        WebPushMessage message = createPushMessage(title, "새 메시지");

        // when
        notificationService.sendPushTo(userId, message, now);

        // then
        Notification log = notificationRepository.findByUserIdAndTitle(userId, title);
        assertThat(log).isNotNull()
                .extracting("userId", "title", "message", "clickTarget", "isRead", "isDeleted")
                .containsExactly(userId, title, "새 메시지", "https://test.com", false, false);
        assertThat(log.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(now.truncatedTo(ChronoUnit.MILLIS));
    }

    @DisplayName("같은 제목의 알림이 추가 전송되면 이전 알림이 업데이트됩니다.")
    @Test
    void sendPushToAgain() throws Exception {
        // given
        Long userId = 1L;
        LocalDateTime beforeNow = LocalDateTime.now().minusMinutes(5);
        String title = "채팅방 이름";
        WebPushMessage message = createPushMessage(title, "새 메시지");
        notificationService.sendPushTo(userId, message, beforeNow);

        LocalDateTime now = LocalDateTime.now();
        message = createPushMessage(title, "추가 메시지");

        // when
        notificationService.sendPushTo(userId, message, now);

        // then
        Notification log = notificationRepository.findByUserIdAndTitle(userId, title);
        assertThat(log).isNotNull()
                .extracting("userId", "title", "message")
                .containsExactly(userId, title, "추가 메시지");
        assertThat(log.getCreatedAt().truncatedTo(ChronoUnit.MILLIS)).isEqualTo(now.truncatedTo(ChronoUnit.MILLIS));
    }

    @DisplayName("알림 목록을 조회합니다.")
    @Test
    void listNotifications() {
        // given
        WebPushMessage message = createPushMessage("채팅방 이름", "새 메시지");
        notificationService.sendPushTo(1L, message, LocalDateTime.now());

        // when
        List<NotificationListResponse> result = notificationService.listNotifications(1L);

        // then
        assertThat(result).hasSize(1);
    }

    private PushSubscribeRequest createPushSubscribeRequest(Long userId, String endPoint) {
        return PushSubscribeRequest.builder()
                .userId(userId)
                .endPoint(endPoint)
                .publicKey("BBOgkXZpGjNfJkoOvJkdc8v5sDXYZJvV4eGxGgx9TEXAMPLE_PUBLIC_KEY")
                .auth("m2s7GdXEXAMPLE_AUTH")
                .deviceName("Chrome on Windows")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private WebPushMessage createPushMessage(String title, String message) {
        return WebPushMessage.builder()
                .title(title)
                .body(message)
                .clickTarget("https://test.com")
                .build();
    }
}