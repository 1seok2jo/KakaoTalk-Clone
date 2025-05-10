package oneseoktwojo.ohtalkhae.domain.profile.service;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import oneseoktwojo.ohtalkhae.domain.auth.repository.UserRepository;
import oneseoktwojo.ohtalkhae.domain.profile.dto.ProfileResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileInfoService {

    private final UserRepository userRepository;

    public void updateNickname(Long userId, String newNickname) {
        if (userRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
        user.setNickname(newNickname);
        userRepository.save(user);
    }

    public void updateStatusMessage(Long userId, String newStatusMessage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
        user.setStatusMessage(newStatusMessage);
        userRepository.save(user);
    }

    public ProfileResponse getProfileInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
        return new ProfileResponse(user);
    }
}