package oneseoktwojo.ohtalkhae;

import oneseoktwojo.ohtalkhae.config.SecurityConfig;
import oneseoktwojo.ohtalkhae.domain.auth.jwt.JWTUtil;
import oneseoktwojo.ohtalkhae.domain.notification.PushNotificationController;
import oneseoktwojo.ohtalkhae.domain.notification.PushNotificationService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {
    PushNotificationController.class
})
@Import(SecurityConfig.class)
public class ControllerTestSupport {

    @MockitoBean
    private PushNotificationService pushNotificationService;

    @MockitoBean
    private JWTUtil jwtUtil;
}
