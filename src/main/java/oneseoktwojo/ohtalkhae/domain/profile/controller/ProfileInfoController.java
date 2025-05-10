package oneseoktwojo.ohtalkhae.domain.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.profile.dto.NicknameUpdateRequest;
import oneseoktwojo.ohtalkhae.domain.profile.service.ProfileInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/{userId}/profile/info")
public class ProfileInfoController {

    private final ProfileInfoService profileInfoService;

    @PutMapping("/nickname")
    public ResponseEntity<String> updateNickname(
            @PathVariable Long userId,
            @Valid @RequestBody NicknameUpdateRequest request) {
        profileInfoService.updateNickname(userId, request.getNickname());
        return ResponseEntity.ok("닉네임이 성공적으로 변경되었습니다.");
    }

    @PutMapping("/status-message")
    public ResponseEntity<String> updateStatusMessage(
            @PathVariable Long userId,
            @Valid @RequestBody StatusMessageUpdateRequest request) {
        profileInfoService.updateStatusMessage(userId, request.getStatusMessage());
        return ResponseEntity.ok("상태 메시지가 성공적으로 변경되었습니다.");
    }
}
