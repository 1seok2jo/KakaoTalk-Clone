package oneseoktwojo.ohtalkhae.domain.emoji;

import oneseoktwojo.ohtalkhae.domain.emoji.dto.request.EmojiRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiDetailResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiListResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiPurchaseCheckResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiRegisterResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.entity.Emoji;
import oneseoktwojo.ohtalkhae.domain.emoji.entity.EmojiImage;
import oneseoktwojo.ohtalkhae.domain.emoji.repository.EmojiImageRepository;
import oneseoktwojo.ohtalkhae.domain.emoji.repository.EmojiRepository;
import oneseoktwojo.ohtalkhae.domain.emoji.service.EmojiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class EmojiServiceTest {

    @Mock
    private EmojiRepository emojiRepository;

    @Mock
    private EmojiImageRepository emojiImageRepository;

    @InjectMocks
    private EmojiService emojiService;

    private Emoji emoji;

    @BeforeEach
    void setUp() {
        emoji = Emoji.builder()
                .emojiName("테스트 이모티콘")
                .emojiPrice(3000)
                .mainEmojiUrl("https://example.com/main.png")
                .sellerName("테스트 판매자")
                .build();
        emoji.setId(1L); // ID를 1로 설정
        emoji.setPurchaseCount(5);
        emoji.setCreatedAt(LocalDateTime.now());
        emoji.setBookmarkedUserIds(new HashSet<>(List.of("user1")));
        emoji.setPurchasedUserIds(new HashSet<>(List.of("user2")));
    }

    @Test
    void testRegisterEmoji_success() {

        EmojiRegisterRequest request = EmojiRegisterRequest.builder()
                .emojiName("테스트 이모티콘")
                .emojiPrice(3000)
                .mainEmojiUrl("https://example.com/main.png")
                .emojiUrls(List.of("https://example.com/1.png", "https://example.com/2.png"))
                .sellerName("테스트 판매자")
                .build();

        // 저장 시 반환값 설정
        when(emojiRepository.save(any(Emoji.class))).thenAnswer(i -> {
            Emoji emoji = i.getArgument(0);
            emoji.setId(1L); // ID를 1로 설정
            return emoji;
        });

        when(emojiImageRepository.saveAll(any(List.class))).thenReturn(null);

        // 서비스 호출
        EmojiRegisterResponse response = emojiService.registerEmoji(request);

        // 검증
        assertNotNull(response.getEmojiId());
        assertEquals("테스트 이모티콘", response.getEmojiName());
        assertEquals("/emojis/1", response.getDetailPageUrl());
    }

    @Test
    void testGetAllEmojis() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(emojiRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(emoji)));

        Page<EmojiListResponse> page = emojiService.getAllEmojis(pageable);

        assertEquals(1, page.getTotalElements());
        assertEquals("테스트 이모티콘", page.getContent().get(0).getEmojiName());
    }

    @Test
    void testGetPopularEmojis() {
        Emoji emoji2 = Emoji.builder().emojiName("인기2").build();
        emoji2.setId(2L);
        emoji2.setPurchaseCount(10);
        when(emojiRepository.findAll()).thenReturn(List.of(emoji, emoji2));

        List<EmojiListResponse> result = emojiService.getPopularEmojis();

        assertEquals(2, result.size());
        assertEquals("인기2", result.get(0).getEmojiName());
    }

    @Test
    void testGetLatestEmojis() {
        Emoji emoji2 = Emoji.builder().emojiName("최신2").build();
        emoji2.setId(2L);
        emoji2.setCreatedAt(LocalDateTime.now().minusDays(1));
        when(emojiRepository.findAll()).thenReturn(List.of(emoji, emoji2));

        List<EmojiListResponse> result = emojiService.getLatestEmojis();

        assertEquals(2, result.size());
        assertEquals("최신2", result.get(0).getEmojiName());
    }

    @Test
    void testSearchEmojis() {
        when(emojiRepository.findAll()).thenReturn(List.of(emoji));
        List<EmojiListResponse> result = emojiService.searchEmojis("테스트");
        assertEquals(1, result.size());
    }

    @Test
    void testGetMyEmojis() {
        when(emojiRepository.findAll()).thenReturn(List.of(emoji));
        List<EmojiListResponse> result = emojiService.getMyEmojis("user2");
        assertEquals(1, result.size());
    }

    @Test
    void testGetBookmarkedEmojis() {
        when(emojiRepository.findAll()).thenReturn(List.of(emoji));
        List<EmojiListResponse> result = emojiService.getBookmarkedEmojis("user1");
        assertEquals(1, result.size());
    }

    @Test
    void testGetEmojiDetail() {
        when(emojiRepository.findById(1L)).thenReturn(Optional.of(emoji));
        when(emojiImageRepository.findByEmojiId(1L)).thenReturn(List.of(
                EmojiImage.builder().imageUrl("url1").sortOrder(1).build(),
                EmojiImage.builder().imageUrl("url2").sortOrder(2).build()
        ));

        EmojiDetailResponse detail = emojiService.getEmojiDetail(1L);

        assertEquals(1L, detail.getEmojiId());
        assertEquals(2, detail.getEmojiUrls().size());
    }

    @Test
    void testAddBookmark() {
        when(emojiRepository.findById(1L)).thenReturn(Optional.of(emoji));
        emoji.getBookmarkedUserIds().remove("user3");
        emojiService.addBookmark("user3", 1L);
        assertTrue(emoji.getBookmarkedUserIds().contains("user3"));
    }

    @Test
    void testRemoveBookmark() {
        when(emojiRepository.findById(1L)).thenReturn(Optional.of(emoji));
        emoji.getBookmarkedUserIds().add("user4");
        emojiService.removeBookmark("user4", 1L);
        assertFalse(emoji.getBookmarkedUserIds().contains("user4"));
    }

    @Test
    void testBuyEmoji_success() {
        when(emojiRepository.findById(1L)).thenReturn(Optional.of(emoji));
        emoji.getPurchasedUserIds().remove("user5");
        int before = emoji.getPurchaseCount();
        emojiService.buyEmoji("user5", 1L);
        assertTrue(emoji.getPurchasedUserIds().contains("user5"));
        assertEquals(before + 1, emoji.getPurchaseCount());
    }

    @Test
    void testBuyEmoji_alreadyPurchased() {
        when(emojiRepository.findById(1L)).thenReturn(Optional.of(emoji));
        emoji.getPurchasedUserIds().add("user6");
        assertThrows(IllegalStateException.class, () -> emojiService.buyEmoji("user6", 1L));
    }

    @Test
    void testCheckPurchase() {
        when(emojiRepository.findById(1L)).thenReturn(Optional.of(emoji));
        EmojiPurchaseCheckResponse resp = emojiService.checkPurchase("user2", 1L);
        assertTrue(resp.isPurchased());
        EmojiPurchaseCheckResponse resp2 = emojiService.checkPurchase("userX", 1L);
        assertFalse(resp2.isPurchased());
    }
}
