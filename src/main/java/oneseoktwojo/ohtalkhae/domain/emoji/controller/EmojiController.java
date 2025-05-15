package oneseoktwojo.ohtalkhae.domain.emoji.controller;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiListResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiRegisterResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.service.EmojiService;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.request.EmojiBuyRequest;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.request.EmojiRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiDetailResponse;
import oneseoktwojo.ohtalkhae.domain.emoji.dto.response.EmojiPurchaseCheckResponse;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emojis")
@RequiredArgsConstructor
public class EmojiController {

    private final EmojiService emojiService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<EmojiRegisterResponse>> registerEmoji(
            @RequestBody EmojiRegisterRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String sellerName = userDetails.getUsername();
        EmojiRegisterResponse response = emojiService.registerEmoji(request, sellerName);
        return ResponseEntity.ok(ApiResponse.success(200, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmojiListResponse>>> getAllEmojis() {
        List<EmojiListResponse> result = emojiService.getAllEmojis();
        return ResponseEntity.ok(ApiResponse.success(200, result));
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<EmojiListResponse>>> getPopularEmojis() {
        List<EmojiListResponse> result = emojiService.getPopularEmojis();
        return ResponseEntity.ok(ApiResponse.success(200, result));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<EmojiListResponse>>> getLatestEmojis() {
        List<EmojiListResponse> result = emojiService.getLatestEmojis();
        return ResponseEntity.ok(ApiResponse.success(200, result));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EmojiListResponse>>> searchEmojis(
            @RequestParam String keyword) {
        List<EmojiListResponse> result = emojiService.searchEmojis(keyword);
        return ResponseEntity.ok(ApiResponse.success(200, result));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<EmojiListResponse>>> getMyEmojis(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        List<EmojiListResponse> result = emojiService.getMyEmojis(userId);
        return ResponseEntity.ok(ApiResponse.success(200, result));
    }

    @GetMapping("/bookmarked")
    public ResponseEntity<ApiResponse<List<EmojiListResponse>>> getBookmarkedEmojis(
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        List<EmojiListResponse> result = emojiService.getBookmarkedEmojis(userId);
        return ResponseEntity.ok(ApiResponse.success(200, result));
    }

    @GetMapping("/{emojiId}")
    public ResponseEntity<ApiResponse<EmojiDetailResponse>> getEmojiDetail(@PathVariable Long emojiId) {
        EmojiDetailResponse result = emojiService.getEmojiDetail(emojiId);
        return ResponseEntity.ok(ApiResponse.success(200, result));
    }

    @PostMapping("/{emojiId}/bookmark")
    public ResponseEntity<ApiResponse<Void>> addBookmark(
            @PathVariable Long emojiId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        emojiService.addBookmark(userId, emojiId);
        return ResponseEntity.ok(ApiResponse.success(200, null));
    }

    @DeleteMapping("/{emojiId}/bookmark")
    public ResponseEntity<ApiResponse<Void>> removeBookmark(
            @PathVariable Long emojiId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        emojiService.removeBookmark(userId, emojiId);
        return ResponseEntity.ok(ApiResponse.success(200, null));
    }

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<Void>> buyEmoji(
            @RequestBody EmojiBuyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        emojiService.buyEmoji(userId, request.getEmojiId());
        return ResponseEntity.ok(ApiResponse.success(200, null));
    }

    @GetMapping("/{emojiId}/purchase/check")
    public ResponseEntity<ApiResponse<EmojiPurchaseCheckResponse>> checkPurchase(
            @PathVariable Long emojiId,
            @AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        EmojiPurchaseCheckResponse result = emojiService.checkPurchase(userId, emojiId);
        return ResponseEntity.ok(ApiResponse.success(200, result));
    }
}
