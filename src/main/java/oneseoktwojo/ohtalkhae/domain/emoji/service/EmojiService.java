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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmojiService {

    private final EmojiRepository emojiRepository;
    private final EmojiImageRepository emojiImageRepository;

    // Emoji → EmojiListResponse 로 자동변환해주는 헬퍼 메소드
    private EmojiListResponse toListResponse(Emoji emoji) {
        return EmojiListResponse.builder()
                .emojiId(emoji.getId())
                .emojiName(emoji.getEmojiName())
                .emojiPrice(emoji.getEmojiPrice())
                .mainEmojiUrl(emoji.getMainEmojiUrl())
                .sellerName(emoji.getSellerName())
                .build();

    }

    @Transactional
    public EmojiRegisterResponse registerEmoji(EmojiRegisterRequest request) {
        // 1. Emoji 엔티티 생성 및 저장
        Emoji emoji = createEmojiFromRequest(request, request.getSellerName());
        emojiRepository.save(emoji);

        // 2. EmojiImage 리스트 생성 및 저장
        List<EmojiImage> emojiImages = createEmojiImages(request.getEmojiUrls(), emoji);
        emojiImageRepository.saveAll(emojiImages);

        // 3. 응답 객체 생성
        return createRegisterResponse(emoji);
    }

    private Emoji createEmojiFromRequest(EmojiRegisterRequest request, String sellerName) {
        return Emoji.builder()
                .emojiName(request.getEmojiName())
                .emojiPrice(request.getEmojiPrice())
                .mainEmojiUrl(request.getMainEmojiUrl())
                .sellerName(sellerName)
                .build();
    }

    private List<EmojiImage> createEmojiImages(List<String> emojiUrls, Emoji emoji) {
        return IntStream.range(0, emojiUrls.size())
                .mapToObj(i -> EmojiImage.builder()
                        .imageUrl(emojiUrls.get(i))
                        .sortOrder(i+1)
                        .emoji(emoji)
                        .build())
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

    // 전체 이모티콘 목록 조회, 페이징 처리
    public Page<EmojiListResponse> getAllEmojis(Pageable pageable) {
        Page<Emoji> emojiPage = emojiRepository.findAll(pageable);
        List<EmojiListResponse> content = emojiPage.stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
        return new PageImpl<>(content, pageable, emojiPage.getTotalElements());
    }

    // 구매횟수(purchaseCount)를 기준으로 내림차순 정렬하여, 상위 10개만 보여줌
    public List<EmojiListResponse> getPopularEmojis() {
        return emojiRepository.findAll().stream()
                .sorted(Comparator.comparingInt(emoji -> -emoji.getPurchaseCount()))
                .limit(10)
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    public List<EmojiListResponse> getLatestEmojis() {
        return emojiRepository.findAll().stream()
                .sorted(Comparator.comparing(Emoji::getCreatedAt).reversed())
                .limit(10)
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    public List<EmojiListResponse> searchEmojis(String keyword) {
        return emojiRepository.findAll().stream()
                .filter(emoji -> emoji.getEmojiName().toLowerCase().contains(keyword.toLowerCase()))
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    public List<EmojiListResponse> getMyEmojis(String userId) {
        return emojiRepository.findAll().stream()
                .filter(emoji -> emoji.getPurchasedUserIds().contains(userId))
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    public List<EmojiListResponse> getBookmarkedEmojis(String userId) {
        return emojiRepository.findAll().stream()
                .filter(emoji -> emoji.getBookmarkedUserIds().contains(userId))
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    public EmojiDetailResponse getEmojiDetail(Long emojiId) {
        Emoji emoji = emojiRepository.findById(emojiId)
                .orElseThrow(() -> new IllegalArgumentException("이모티콘을 찾을 수 없습니다."));

        List<String> emojiUrls = emojiImageRepository.findByEmojiId(emojiId).stream()
                .sorted(Comparator.comparingInt(EmojiImage::getSortOrder))
                .map(EmojiImage::getImageUrl)
                .collect(Collectors.toList());

        return EmojiDetailResponse.builder()
                .emojiId(emoji.getId())
                .emojiName(emoji.getEmojiName())
                .emojiPrice(emoji.getEmojiPrice())
                .mainEmojiUrl(emoji.getMainEmojiUrl())
                .sellerName(emoji.getSellerName())
                .emojiUrls(emojiUrls)
                .build();
    }

    public void addBookmark(String userId, Long emojiId) {
        Emoji emoji = emojiRepository.findById(emojiId)
                .orElseThrow(() -> new IllegalArgumentException("이모티콘을 찾을 수 없습니다."));
        // 중복 북마크 방지
        if(!emoji.getBookmarkedUserIds().contains(userId)) {
            emoji.getBookmarkedUserIds().add(userId);
            emojiRepository.save(emoji);
        }
    }

    public void removeBookmark(String userId, Long emojiId) {
        Emoji emoji = emojiRepository.findById(emojiId)
                .orElseThrow(() -> new IllegalArgumentException("이모티콘을 찾을 수 없습니다."));
        // 북마크 해제 하려는게 해당 유저가 맞는지 확인
        if(emoji.getBookmarkedUserIds().contains(userId)) {
            emoji.getBookmarkedUserIds().remove(userId);
            emojiRepository.save(emoji);
        }
    }

    @Transactional // 구매는 중요하므로 트랜잭션 처리
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

    // 캐싱 적용, redis 같은거 없이 기본 메모리 캐시로만 사용
    @Cacheable(value = "emojiPurchaseCheck", key = "#userId + '_' + #emojiId")
    public EmojiPurchaseCheckResponse checkPurchase(String userId, Long emojiId) {
        Emoji emoji = emojiRepository.findById(emojiId)
                .orElseThrow(() -> new IllegalArgumentException("이모티콘을 찾을 수 없습니다."));
        boolean purchased = emoji.getPurchasedUserIds().contains(userId);
        return new EmojiPurchaseCheckResponse(purchased);
    }


}
