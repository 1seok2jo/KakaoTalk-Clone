package oneseoktwojo.ohtalkhae;

import oneseoktwojo.ohtalkhae.config.SecurityConfig;
import oneseoktwojo.ohtalkhae.domain.auth.jwt.JWTUtil;
import oneseoktwojo.ohtalkhae.domain.auth.service.RefreshTokenService;
import oneseoktwojo.ohtalkhae.domain.notification.NotificationController;
import oneseoktwojo.ohtalkhae.domain.notification.NotificationService;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(controllers = {
    NotificationController.class
})
@Import(SecurityConfig.class)
public class ControllerTestSupport {

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private JWTUtil jwtUtil;
}
