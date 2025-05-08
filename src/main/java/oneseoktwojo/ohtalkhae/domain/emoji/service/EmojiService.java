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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private final EmojiRepository emojiRepository;
    private final EmojiImageRepository emojiImageRepository;

    public EmojiRegisterResponse registerEmoji(EmojiRegisterRequest request, String sellerName) {
        // 1. Emoji 엔티티 생성 및 저장
        Emoji emoji = new Emoji(
                request.getEmojiName(),
                request.getEmojiPrice(),
                request.getMainEmojiUrl(),
                sellerName
        );
        emojiRepository.save(emoji);

        // 2. EmojiImage 리스트 생성 및 저장
        List<EmojiImage> images = new ArrayList<>();
        List<String> emojiUrls = request.getEmojiUrls();
        for (int i = 0; i < emojiUrls.size(); i++) {
            String imageUrl = emojiUrls.get(i);
            EmojiImage image = new EmojiImage(imageUrl, i + 1, emoji);
            images.add(image);
        }
        emojiImageRepository.saveAll(images);

        // 3. 응답 객체 생성
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

    public List<EmojiListResponse> getPopularEmojis() {
        // TODO: 인기 이모티콘 조회 로직
        return List.of();
    }

    public List<EmojiListResponse> getLatestEmojis() {
        // TODO: 최신 이모티콘 조회 로직
        return List.of();
    }

    public List<EmojiListResponse> searchEmojis(String keyword) {
        // TODO: 이모티콘 검색 로직
        return List.of();
    }

    public List<EmojiListResponse> getMyEmojis(String userId) {
        // TODO: 사용자가 구매한 이모티콘 목록 조회 로직
        return List.of();
    }

    public List<EmojiListResponse> getBookmarkedEmojis(String userId) {
        // TODO: 찜한 이모티콘 목록 조회 로직
        return List.of();
    }

    public EmojiDetailResponse getEmojiDetail(Long emojiId) {
        // TODO: 이모티콘 상세 정보 조회 로직
        return new EmojiDetailResponse();
    }

    public void addBookmark(String userId, Long emojiId) {
        // TODO: 찜 등록 처리 로직
    }

    public void removeBookmark(String userId, Long emojiId) {
        // TODO: 찜 해제 처리 로직
    }

    public void buyEmoji(String userId, Long emojiId) {
        // TODO: 이모티콘 구매 처리 로직
    }

    public EmojiPurchaseCheckResponse checkPurchase(String userId, Long emojiId) {
        // TODO: 이모티콘 구매 여부 확인 로직
        return new EmojiPurchaseCheckResponse();
    }
}
