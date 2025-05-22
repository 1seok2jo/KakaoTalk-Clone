package oneseoktwojo.ohtalkhae.domain.emoji.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmojiImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private int sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emoji_id")
    private Emoji emoji;

    @Builder
    public EmojiImage(String imageUrl, int sortOrder, Emoji emoji) {
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.emoji = emoji;
    }
}
