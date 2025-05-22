package oneseoktwojo.ohtalkhae.domain.emoji.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class EmojiRegisterRequest {

    @NotBlank(message = "판매자 이름은 필수입니다.")
    private String sellerName;

    @NotBlank(message = "이모티콘 이름은 필수입니다.")
    private String emojiName;

    @Min(value = 100, message = "이모티콘 가격은 100원 이상이어야 합니다.")
    private int emojiPrice;

    @NotBlank(message = "대표 이미지 URL은 필수입니다.")
    private String mainEmojiUrl;

    @NotEmpty(message = "이모티콘 이미지는 1개 이상 등록해야 합니다.")
    private List<@NotBlank(message = "이모티콘 이미지 URL은 비어있을 수 없습니다.") String> emojiUrls;

    @Builder
    public EmojiRegisterRequest(String emojiName, int emojiPrice, String mainEmojiUrl, List<String> emojiUrls, String sellerName) {
        this.emojiName = emojiName;
        this.emojiPrice = emojiPrice;
        this.mainEmojiUrl = mainEmojiUrl;
        this.emojiUrls = emojiUrls;
        this.sellerName = sellerName;
    }
}