package oneseoktwojo.ohtalkhae.domain.emoji.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmojiDetailResponse {
    private Long emojiId;
    private String emojiName;
    private int emojiPrice;
    private String mainEmojiUrl;
    private String sellerName;
    private List<String> emojiUrls;
}
