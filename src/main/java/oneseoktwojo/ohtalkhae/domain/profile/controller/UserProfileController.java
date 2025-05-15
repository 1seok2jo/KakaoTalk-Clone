package oneseoktwojo.ohtalkhae.domain.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.profile.dto.ProfileVisibilityUpdateRequest;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import oneseoktwojo.ohtalkhae.domain.profile.dto.ProfileBackgroundUpdateRequest;
import oneseoktwojo.ohtalkhae.domain.profile.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @PostMapping("/image")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(
            @PathVariable Long userId,
            @RequestParam("image") MultipartFile file) throws IOException {
        String uploadedFileName = userProfileService.uploadProfileImage(userId, file);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "프로필 사진이 성공적으로 업로드되었습니다. 파일 이름: " + uploadedFileName));
    }

    @PutMapping("/background")
    public ResponseEntity<ApiResponse<String>> updateProfileBackground(
            @PathVariable Long userId,
            @Valid @ModelAttribute ProfileBackgroundUpdateRequest request) throws IOException {
        userProfileService.updateProfileBackground(userId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "프로필 배경이 성공적으로 변경되었습니다."));
    }

    @PutMapping("/visibility")
    public ResponseEntity<ApiResponse<String>> updateProfileVisibility(
            @PathVariable Long userId,
            @Valid @RequestBody ProfileVisibilityUpdateRequest request) {
        userProfileService.updateProfileVisibility(userId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "프로필 공개 여부가 성공적으로 변경되었습니다."));
    }
}