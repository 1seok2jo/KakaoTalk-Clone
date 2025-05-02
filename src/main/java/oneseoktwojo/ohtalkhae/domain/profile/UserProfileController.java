package oneseoktwojo.ohtalkhae.domain.profile;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<String> uploadProfileImage(@PathVariable Long userId, @RequestParam("image") MultipartFile file) {
        try {
            String uploadedFileName = userProfileService.uploadProfileImage(userId, file);
            return ResponseEntity.ok("프로필 사진이 성공적으로 업로드되었습니다. 파일 이름: " + uploadedFileName);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("프로필 사진 업로드에 실패했습니다 : " + e.getMessage());
        }
    }
}