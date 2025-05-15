package oneseoktwojo.ohtalkhae.domain.profile.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import oneseoktwojo.ohtalkhae.domain.auth.repository.UserRepository;
import oneseoktwojo.ohtalkhae.domain.profile.dto.ProfileBackgroundUpdateRequest;
import oneseoktwojo.ohtalkhae.domain.profile.dto.ProfileVisibilityUpdateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    private final UserRepository userRepository;

    public String uploadProfileImage(Long userId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
        if (!allowedExtensions.contains(fileExtension)) {
            throw new IllegalArgumentException("허용되지 않은 파일 확장자입니다. : " + fileExtension);
        }

        String newFilename = UUID.randomUUID().toString() + fileExtension;
        Path targetPath = Paths.get(uploadPath, newFilename);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
        user.setProfileImagePath(newFilename);
        userRepository.save(user);

        return newFilename;
    }

    public void updateProfileBackground(Long userId, ProfileBackgroundUpdateRequest request) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));

        // 배경 설명 업데이트
        String backgroundDescription = request.getBackgroundDescription();
        user.setProfileBackgroundDescription(backgroundDescription);

        // 배경 이미지 업데이트 (이미지가 있는 경우)
        MultipartFile backgroundImage = request.getBackgroundImage();
        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            String originalFilename = backgroundImage.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();

            List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");
            if (!allowedExtensions.contains(fileExtension)) {
                throw new IllegalArgumentException("허용되지 않은 파일 확장자 입니다. : " + fileExtension);
            }

            // 기존 배경 이미지 삭제
            String oldBackgroundImagePath = user.getProfileBackgroundImagePath();
            if (oldBackgroundImagePath != null) {
                Path oldFilePath = Paths.get(uploadPath, oldBackgroundImagePath);
                Files.deleteIfExists(oldFilePath);
            }

            String newFilename = UUID.randomUUID().toString() + fileExtension;
            Path targetPath = Paths.get(uploadPath, newFilename);
            Files.copy(backgroundImage.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            user.setProfileBackgroundImagePath(newFilename);
        }

        userRepository.save(user);
    }

    public void updateProfileVisibility(Long userId, ProfileVisibilityUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID:" + userId));
        user.setPublic(request.isPublic());
        userRepository.save(user);
    }
}