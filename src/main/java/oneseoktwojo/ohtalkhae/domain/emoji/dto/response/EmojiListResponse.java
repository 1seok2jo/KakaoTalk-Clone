package oneseoktwojo.ohtalkhae.domain.emoji.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmojiListResponse {
    private Long emojiId;
    private String emojiName;
    private int emojiPrice;
    private String mainEmojiUrl;
    private String sellerName;
}