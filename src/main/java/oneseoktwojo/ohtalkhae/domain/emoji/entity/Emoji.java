package oneseoktwojo.ohtalkhae.domain.emoji.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Emoji {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emojiName;

    private int emojiPrice;

    private String mainEmojiUrl;

    private String sellerName;

    private LocalDateTime createdAt;

    public Emoji(String emojiName, int emojiPrice, String mainEmojiUrl, String sellerName) {
        this.emojiName = emojiName;
        this.emojiPrice = emojiPrice;
        this.mainEmojiUrl = mainEmojiUrl;
        this.sellerName = sellerName;
        this.createdAt = LocalDateTime.now();
    }

    // EmojiImage랑 연결
    @OneToMany(mappedBy = "emoji", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmojiImage> emojiImages = new ArrayList<>();
}