package oneseoktwojo.ohtalkhae.domain.notification;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.notification.dto.PushSubscribeRequest;
import oneseoktwojo.ohtalkhae.domain.notification.mapper.PushSubscriptionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PushNotificationService {

    private final PushSubscriptionRepository pushSubscriptionRepository;
    private final PushSubscriptionMapper pushSubscriptionMapper;

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

}
