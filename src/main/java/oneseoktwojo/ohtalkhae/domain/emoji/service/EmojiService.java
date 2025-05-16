package oneseoktwojo.ohtalkhae.domain.emoji.service;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.request.EmojiRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiDetailResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiListResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiPurchaseCheckResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiRegisterResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.entity.Emoji;
import oneseoktwojo.ohtalkhae.domain.emoji.entity.EmojiImage;
import oneseoktwojo.ohtalkhae.domain.emoji.repository.EmojiImageRepository;
import oneseoktwojo.ohtalkhae.domain.emoji.repository.EmojiRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private final EmojiRepository emojiRepository;
    private final EmojiImageRepository emojiImageRepository;

    // Emoji → EmojiListResponse 로 자동변환해주는 헬퍼 메소드
    private EmojiListResponse toListResponse(Emoji emoji) {
        return new EmojiListResponse(
                emoji.getId(),
                emoji.getEmojiName(),
                emoji.getEmojiPrice(),
                emoji.getMainEmojiUrl(),
                emoji.getSellerName()
        );
    }

    public EmojiRegisterResponse registerEmoji(EmojiRegisterRequest request, String sellerName) {
        // 1. Emoji 엔티티 생성 및 저장
        Emoji emoji = createEmojiFromRequest(request, sellerName);
        emojiRepository.save(emoji);

        // 2. EmojiImage 리스트 생성 및 저장
        List<EmojiImage> emojiImages = createEmojiImages(request.getEmojiUrls(), emoji);
        emojiImageRepository.saveAll(emojiImages);

        // 3. 응답 객체 생성
        return createRegisterResponse(emoji);
    }

    private Emoji createEmojiFromRequest(EmojiRegisterRequest request, String sellerName) {
        return new Emoji(
                request.getEmojiName(),
                request.getEmojiPrice(),
                request.getMainEmojiUrl(),
                sellerName
        );
    }

    private List<EmojiImage> createEmojiImages(List<String> emojiUrls, Emoji emoji) {
        return IntStream.range(0, emojiUrls.size())
                .mapToObj(i -> new EmojiImage(emojiUrls.get(i), i + 1, emoji))
                .collect(Collectors.toList());
    }

    private EmojiRegisterResponse createRegisterResponse(Emoji emoji) {
        String detailPageUrl = "/emojis/" + emoji.getId();
        return new EmojiRegisterResponse(
                emoji.getId(),
                emoji.getEmojiName(),
                detailPageUrl
        );

    }

    public List<EmojiListResponse> getAllEmojis() {
        return emojiRepository.findAll().stream()
                .map(emoji -> new EmojiListResponse(
                        emoji.getId(),
                        emoji.getEmojiName(),
                        emoji.getEmojiPrice(),
                        emoji.getMainEmojiUrl(),
                        emoji.getSellerName()
                ))
                .collect(Collectors.toList());
    }

    // 구매횟수(purchaseCount)를 기준으로 내림차순 정렬하여, 상위 10개만 보여줌
    public List<EmojiListResponse> getPopularEmojis() {
        return emojiRepository.findAll().stream()
                .sorted(Comparator.comparingInt(emoji -> -emoji.getPurchaseCount()))
                .limit(10)
                .map(emoji -> new EmojiListResponse(
                        emoji.getId(),
                        emoji.getEmojiName(),
                        emoji.getEmojiPrice(),
                        emoji.getMainEmojiUrl(),
                        emoji.getSellerName()
                ))
                .collect(Collectors.toList());
    }

    public List<EmojiListResponse> getLatestEmojis() {
        return emojiRepository.findAll().stream()
                .sorted(Comparator.comparing(Emoji::getCreatedAt).reversed())
                .limit(10)
                .map(emoji -> new EmojiListResponse(
                        emoji.getId(),
                        emoji.getEmojiName(),
                        emoji.getEmojiPrice(),
                        emoji.getMainEmojiUrl(),
                        emoji.getSellerName()
                ))
                .collect(Collectors.toList());
    }

    public List<EmojiListResponse> searchEmojis(String keyword) {
        return emojiRepository.findAll().stream()
                .filter(emoji -> emoji.getEmojiName().toLowerCase().contains(keyword.toLowerCase()))
                .map(emoji -> new EmojiListResponse(
                        emoji.getId(),
                        emoji.getEmojiName(),
                        emoji.getEmojiPrice(),
                        emoji.getMainEmojiUrl(),
                        emoji.getSellerName()
                ))
                .collect(Collectors.toList());
    }

    public List<EmojiListResponse> getMyEmojis(String userId) {
        return emojiRepository.findAll().stream()
                .filter(emoji -> emoji.getPurchasedUserIds().contains(userId))
                .map(emoji -> new EmojiListResponse(
                        emoji.getId(),
                        emoji.getEmojiName(),
                        emoji.getEmojiPrice(),
                        emoji.getMainEmojiUrl(),
                        emoji.getSellerName()
                ))
                .collect(Collectors.toList());
    }

    public List<EmojiListResponse> getBookmarkedEmojis(String userId) {
        return emojiRepository.findAll().stream()
                .filter(emoji -> emoji.getBookmarkedUserIds().contains(userId))
                .map(emoji -> new EmojiListResponse(
                        emoji.getId(),
                        emoji.getEmojiName(),
                        emoji.getEmojiPrice(),
                        emoji.getMainEmojiUrl(),
                        emoji.getSellerName()
                ))
                .collect(Collectors.toList());
    }

    public EmojiDetailResponse getEmojiDetail(Long emojiId) {
        Emoji emoji = emojiRepository.findById(emojiId)
                .orElseThrow(() -> new IllegalArgumentException("이모티콘을 찾을 수 없습니다."));

        List<String> emojiUrls = emojiImageRepository.findAll().stream()
                .filter(image -> image.getEmoji().getId().equals(emojiId))
                .sorted(Comparator.comparingInt(EmojiImage::getSortOrder))
                .map(EmojiImage::getImageUrl)
                .collect(Collectors.toList());

        return new EmojiDetailResponse(
                emoji.getId(),
                emoji.getEmojiName(),
                emoji.getEmojiPrice(),
                emoji.getMainEmojiUrl(),
                emoji.getSellerName(),
                emojiUrls);
    }

    public void addBookmark(String userId, Long emojiId) {
        Emoji emoji = emojiRepository.findById(emojiId)
                .orElseThrow(() -> new IllegalArgumentException("이모티콘을 찾을 수 없습니다."));
        emoji.getBookmarkedUserIds().add(userId);
        emojiRepository.save(emoji);
    }

    public void removeBookmark(String userId, Long emojiId) {
        Emoji emoji = emojiRepository.findById(emojiId)
                .orElseThrow(() -> new IllegalArgumentException("이모티콘을 찾을 수 없습니다."));
        emoji.getBookmarkedUserIds().remove(userId);
        emojiRepository.save(emoji);
    }

    public void buyEmoji(String userId, Long emojiId) {
        Emoji emoji = emojiRepository.findById(emojiId)
                .orElseThrow(() -> new IllegalArgumentException("이모티콘을 찾을 수 없습니다."));
        if (emoji.getPurchasedUserIds().contains(userId)) {
            throw new IllegalStateException("이미 구매한 이모티콘입니다.");
        }
        emoji.getPurchasedUserIds().add(userId);
        emoji.incrementPurchaseCount();
        emojiRepository.save(emoji);
    }

    public EmojiPurchaseCheckResponse checkPurchase(String userId, Long emojiId) {
        Emoji emoji = emojiRepository.findById(emojiId)
                .orElseThrow(() -> new IllegalArgumentException("이모티콘을 찾을 수 없습니다."));
        boolean purchased = emoji.getPurchasedUserIds().contains(userId);
        return new EmojiPurchaseCheckResponse(purchased);
    }


}
