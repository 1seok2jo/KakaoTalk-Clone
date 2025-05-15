package oneseoktwojo.ohtalkhae.domain.notification.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class WebPushMessage {

    public String title;
    public String body;
    public String clickTarget;

    @Builder
    public WebPushMessage(String title, String body, String clickTarget) {
        this.title = title;
        this.body = body;
        this.clickTarget = clickTarget;
    }
}
