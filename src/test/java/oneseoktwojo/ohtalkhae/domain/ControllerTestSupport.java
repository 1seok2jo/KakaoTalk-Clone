package oneseoktwojo.ohtalkhae.domain;

import oneseoktwojo.ohtalkhae.domain.notification.PushNotificationController;
import oneseoktwojo.ohtalkhae.domain.notification.PushNotificationService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {
    PushNotificationController.class
})
public class ControllerTestSupport {

    @MockitoBean
    private PushNotificationService pushNotificationService;
}
