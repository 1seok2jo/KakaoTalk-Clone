package oneseoktwojo.ohtalkhae.domain.emoji.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
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

    private int purchaseCount = 0;

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> purchasedUserIds = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    private Set<String> bookmarkedUserIds = new HashSet<>();

    // EmojiImage랑 연결
    @OneToMany(mappedBy = "emoji", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmojiImage> emojiImages = new ArrayList<>();

    @Builder
    public Emoji(String emojiName, int emojiPrice, String mainEmojiUrl, String sellerName) {
        this.emojiName = emojiName;
        this.emojiPrice = emojiPrice;
        this.mainEmojiUrl = mainEmojiUrl;
        this.sellerName = sellerName;
        this.createdAt = java.time.LocalDateTime.now();
    }

    public void incrementPurchaseCount() {
        this.purchaseCount++;
    }
}