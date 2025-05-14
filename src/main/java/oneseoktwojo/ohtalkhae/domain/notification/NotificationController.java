package oneseoktwojo.ohtalkhae.domain.notification;

import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.notification.dto.request.PushSubscribeRequest;
import oneseoktwojo.ohtalkhae.domain.notification.dto.response.DeviceListResponse;
import oneseoktwojo.ohtalkhae.domain.notification.dto.response.NotificationListResponse;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/subscribe")
    public void subscribe(@RequestHeader(value = "User-Agent", required = false) String ua,
                          @RequestBody PushSubscribeRequest request) {
        // 장치 식별용 이름 설정
        request.setDeviceName(parseDeviceName(ua));
        request.setCreatedAt(LocalDateTime.now());

        notificationService.subscribe(request);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribe(@RequestBody PushSubscribeRequest request) {
        notificationService.unsubscribe(request);
    }

    @GetMapping("/devices")
    public ApiResponse<List<DeviceListResponse>> getSubscribedDevices(@RequestParam Long userId) {
        List<DeviceListResponse> subscribedDevices = notificationService.getSubscribedDevices(userId);
        return ApiResponse.success(200, subscribedDevices);
    }

    @GetMapping("/")
    public ApiResponse<List<NotificationListResponse>> getNotificationList(@RequestParam Long userId) {
        return ApiResponse.success(200, notificationService.listNotifications(userId));
    }

    @PutMapping("/{notificationId}")
    public ApiResponse<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ApiResponse.success(200, null);
    }

    @DeleteMapping("/{notificationId}")
    public ApiResponse<Void> markAsDeleted(@PathVariable Long notificationId) {
        notificationService.markNotificationAsDeleted(notificationId);
        return ApiResponse.success(200, null);
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
