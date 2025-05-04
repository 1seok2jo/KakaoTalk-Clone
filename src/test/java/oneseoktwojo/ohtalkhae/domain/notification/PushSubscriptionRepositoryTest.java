package oneseoktwojo.ohtalkhae.domain.notification;

import oneseoktwojo.ohtalkhae.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;

class PushSubscriptionRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private PushSubscriptionRepository pushSubscriptionRepository;

    @AfterEach
    void tearDown() {
        pushSubscriptionRepository.deleteAllInBatch();
    }

    @DisplayName("userId를 이용하여 푸시 알림 구독 목록을 조회합니다.")
    @Test
    void findByUserId() {
        // given
        Long userId1 = 1L;
        Long userId2 = 2L;
        String endPoint1 = "https://fcm.googleapis.com/fcm/send/cx1Ykq8bSxE:APA91bF3YX98k0p-EXAMPLE_END_POINT_USER1";
        String endPoint2 = "https://fcm.googleapis.com/fcm/send/cx1Ykq8bSxE:APA91bF3YX98k0p-EXAMPLE_END_POINT_USER2";

        pushSubscriptionRepository.save(createPushSubscription(userId1, endPoint1));
        pushSubscriptionRepository.save(createPushSubscription(userId2, endPoint2));

        // when
        List<PushSubscription> found = pushSubscriptionRepository.findByUserId(userId1);

        // then
        assertThat(found).hasSize(1)
                .extracting("userId", "endPoint")
                .containsExactly(tuple(userId1, endPoint1));
    }

    @DisplayName("endPoint를 이용하여 푸시 알림 구독 목록을 조회합니다.")
    @Test
    void findByEndPoint() {
        // given
        Long userId1 = 1L;
        Long userId2 = 2L;
        String endPoint1 = "https://fcm.googleapis.com/fcm/send/cx1Ykq8bSxE:APA91bF3YX98k0p-EXAMPLE_END_POINT_USER1";
        String endPoint2 = "https://fcm.googleapis.com/fcm/send/cx1Ykq8bSxE:APA91bF3YX98k0p-EXAMPLE_END_POINT_USER2";

        pushSubscriptionRepository.save(createPushSubscription(userId1, endPoint1));
        pushSubscriptionRepository.save(createPushSubscription(userId2, endPoint2));

        // when
        PushSubscription found = pushSubscriptionRepository.findByEndPoint(endPoint1).orElseThrow();

        // then
        assertThat(found)
                .extracting("userId", "endPoint")
                .containsExactly(userId1, endPoint1);
    }

    private PushSubscription createPushSubscription(Long userId, String endPoint) {
        return PushSubscription.builder()
                .userId(userId)
                .endPoint(endPoint)
                .publicKey("BBOgkXZpGjNfJkoOvJkdc8v5sDXYZJvV4eGxGgx9TEXAMPLE_PUBLIC_KEY")
                .auth("m2s7GdXEXAMPLE_AUTH")
                .deviceName("Chrome on Windows")
                .createdAt(LocalDateTime.now())
                .build();
    }
}