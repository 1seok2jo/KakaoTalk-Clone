package oneseoktwojo.ohtalkhae.domain.notification;

import oneseoktwojo.ohtalkhae.IntegrationTestSupport;
import oneseoktwojo.ohtalkhae.domain.notification.dto.PushSubscribeRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class PushNotificationServiceTest extends IntegrationTestSupport {

    @Autowired
    PushNotificationService pushNotificationService;
    @Autowired
    PushSubscriptionRepository pushSubscriptionRepository;

    @AfterEach
    void tearDown() {
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
        pushNotificationService.subscribe(request);

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
        PushSubscription saved = pushNotificationService.subscribe(request);

        // when
        pushNotificationService.unsubscribe(request);

        // then
        Optional<PushSubscription> opSubscription = pushSubscriptionRepository.findById(saved.getSubscriptionId());
        assertThat(opSubscription.isPresent()).isEqualTo(false);
    }

    @DisplayName("존재하지 않는 웹 푸시 구독 정보를 삭제해도 예외를 던지지 않습니다.")
    @Test
    void unsubscribeNotExist() {
        // given
        Long userId = 1L;
        String endPoint = "https://fcm.googleapis.com/fcm/send/cx1Ykq8bSxE:APA91bF3YX98k0p-EXAMPLE_END_POINT";
        PushSubscribeRequest request = createPushSubscribeRequest(userId, endPoint);

        // when then
        pushNotificationService.unsubscribe(request);
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
}