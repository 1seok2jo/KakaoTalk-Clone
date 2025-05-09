package oneseoktwojo.ohtalkhae.domain.profile.controller;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.profile.service.UserProfileService;
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
    public ResponseEntity<String> uploadProfileImage(@PathVariable Long userId, @RequestParam("image") MultipartFile file) throws IOException {
        String uploadedFileName = userProfileService.uploadProfileImage(userId, file);
        return ResponseEntity.ok("프로필 사진이 성공적으로 업로드되었습니다. 파일 이름: " + uploadedFileName);
    }
}