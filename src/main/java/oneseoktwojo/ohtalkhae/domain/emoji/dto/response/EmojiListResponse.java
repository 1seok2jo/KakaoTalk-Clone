package oneseoktwojo.ohtalkhae.domain.emoji.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmojiListResponse {
    private Long emojiId;
    private String emojiName;
    private int emojiPrice;
    private String mainEmojiUrl;
    private String sellerName;
}