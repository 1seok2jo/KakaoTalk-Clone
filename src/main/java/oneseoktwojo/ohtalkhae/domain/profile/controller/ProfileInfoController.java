package oneseoktwojo.ohtalkhae.domain.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import oneseoktwojo.ohtalkhae.domain.profile.dto.NicknameUpdateRequest;
import oneseoktwojo.ohtalkhae.domain.profile.dto.ProfileResponse;
import oneseoktwojo.ohtalkhae.domain.profile.dto.StatusMessageUpdateRequest;
import oneseoktwojo.ohtalkhae.domain.profile.service.ProfileInfoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/profile/info")
public class ProfileInfoController {

    private final ProfileInfoService profileInfoService;

    @PutMapping("/nickname")
    public ResponseEntity<ApiResponse<String>> updateNickname(
            @PathVariable Long userId,
            @Valid @RequestBody NicknameUpdateRequest request) {
        profileInfoService.updateNickname(userId, request.getNickname());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "닉네임이 성공적으로 변경되었습니다."));
    }

    @PutMapping("/status-message")
    public ResponseEntity<ApiResponse<String>> updateStatusMessage(
            @PathVariable Long userId,
            @Valid @RequestBody StatusMessageUpdateRequest request) {
        profileInfoService.updateStatusMessage(userId, request.getStatusMessage());
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), "상태 메시지가 성공적으로 변경되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileInfo(@PathVariable Long userId){
        ProfileResponse profileResponse = profileInfoService.getProfileInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK.value(), profileResponse));
    }
}
