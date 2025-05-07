package oneseoktwojo.ohtalkhae.domain.emoji.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class EmojiRegisterRequest {
    private String sellerName;
    private String emojiName;
    private int emojiPrice;
    private String mainEmojiUrl;
    private List<String> emojiUrls;
}