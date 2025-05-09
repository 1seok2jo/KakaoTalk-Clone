package oneseoktwojo.ohtalkhae.domain.emoji.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmojiRegisterResponse {
    private Long emojiId;
    private String emojiName;
    private String detailPageUrl;
}