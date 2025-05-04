package oneseoktwojo.ohtalkhae.domain.notification;

import oneseoktwojo.ohtalkhae.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PushNotificationControllerTest extends ControllerTestSupport {

    @Autowired
    MockMvc mockMvc;

    @DisplayName("푸시 알림을 구독한다.")
    @Test
    void subscribe() throws Exception {
        // given
        MockHttpServletRequestBuilder request = post("/notification/subscribe")
                .contentType("application/json")
                .header("User-Agent", getChromeUserAgent())
                .content(getSubscribePayload(1L));

        // when then
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @DisplayName("푸시 알림을 구독 해제한다.")
    @Test
    void unsubscribe() throws Exception {
        // given
        MockHttpServletRequestBuilder request = post("/notification/unsubscribe")
                .contentType("application/json")
                .header("User-Agent", getChromeUserAgent())
                .content(getSubscribePayload(1L));

        // when then
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    private String getSubscribePayload(Long userId) {
        return String.format("""
                {
                  "userId": %s,
                  "endPoint": "https://fcm.googleapis.com/fcm/send/abc123",
                  "publicKey": "BOPGyourPublicKey",
                  "auth": "yourAuthSecret"
                }
        """, userId);
    }

    private String getChromeUserAgent() {
        return "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
    }
}