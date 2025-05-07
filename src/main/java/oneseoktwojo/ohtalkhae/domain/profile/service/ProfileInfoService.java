package oneseoktwojo.ohtalkhae.domain.profile.service;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.profile.UserRepository;
import oneseoktwojo.ohtalkhae.domain.profile.dto.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileInfoService {

    private final UserRepository userRepository;

    public void updateNickname(Long userId, String newNickname) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
        user.setNickname(newNickname);
        userRepository.save(user);
    }
}
