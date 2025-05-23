package oneseoktwojo.ohtalkhae.domain.emoji.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EmojiBuyRequest {

    @NotNull
    private Long emojiId;

    @Builder
    public EmojiBuyRequest(Long emojiId) {
        this.emojiId = emojiId;
    }
}
