package oneseoktwojo.ohtalkhae.domain.profile.service;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.AuthRepository;
import oneseoktwojo.ohtalkhae.domain.auth.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileInfoService {

    private final AuthRepository authRepository;

    public void updateNickname(Long userId, String newNickname) {

        if (authRepository.existsByNickname(newNickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        User user = authRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다. ID: " + userId));
        user.setNickname(newNickname);
        authRepository.save(user);
    }
}
