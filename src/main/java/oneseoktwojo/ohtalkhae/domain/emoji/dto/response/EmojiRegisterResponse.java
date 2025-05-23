package oneseoktwojo.ohtalkhae.domain.emoji.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmojiRegisterResponse {
    private Long emojiId;
    private String emojiName;
    private String detailPageUrl;
}