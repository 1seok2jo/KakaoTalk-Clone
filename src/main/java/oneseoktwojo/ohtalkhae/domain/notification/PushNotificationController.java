package oneseoktwojo.ohtalkhae.domain.notification;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.notification.dto.PushSubscribeRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class PushNotificationController {

    private final PushNotificationService pushNotificationService;

    @PostMapping("/subscribe")
    public void subscribe(@RequestHeader(value = "User-Agent", required = false) String ua,
                          @RequestBody PushSubscribeRequest request) {
        // 장치 식별용 이름 설정
        request.setDeviceName(parseDeviceName(ua));
        request.setCreatedAt(LocalDateTime.now());

        pushNotificationService.subscribe(request);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe(@RequestBody PushSubscribeRequest request) {
        pushNotificationService.unsubscribe(request);
    }

    private String parseDeviceName(String userAgentString) {
        if (userAgentString != null && !userAgentString.isBlank()) {
            UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
            String browser = userAgent.getBrowser().getName();
            String os = userAgent.getOperatingSystem().getName();
            return browser + " on " + os;
        }
        else {
            return "Unknown";
        }
    }
}
