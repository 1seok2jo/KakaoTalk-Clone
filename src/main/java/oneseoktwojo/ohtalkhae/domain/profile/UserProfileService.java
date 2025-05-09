package oneseoktwojo.ohtalkhae.domain.profile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import oneseoktwojo.ohtalkhae.domain.auth.repository.UserRepository;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;

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
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + fileExtension;
        Path targetPath = Paths.get(uploadPath, newFilename);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
        user.setProfileImagePath(newFilename);
        userRepository.save(user);

        return newFilename;
    }
}