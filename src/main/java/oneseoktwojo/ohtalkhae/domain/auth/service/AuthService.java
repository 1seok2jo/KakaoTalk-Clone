package oneseoktwojo.ohtalkhae.domain.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import oneseoktwojo.ohtalkhae.domain.auth.enums.Role;
import oneseoktwojo.ohtalkhae.domain.auth.enums.UserRegisterResult;
import oneseoktwojo.ohtalkhae.domain.auth.mapper.UserMapper;
import oneseoktwojo.ohtalkhae.domain.auth.repository.AuthRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserRegisterResult register(UserRegisterRequest request) {
        if (authRepository.existsByUsername(request.getUsername())) {
            return UserRegisterResult.DUPLICATED_USERNAME;
        } else if (authRepository.existsByEmail(request.getEmail())) {
            return UserRegisterResult.DUPLICATED_EMAIL;
        } else if (authRepository.existsByPhone(request.getPhone())) {
            return UserRegisterResult.DUPLICATED_PHONE;
        }

        User user = UserMapper.INSTANCE.toUser(request);

        user.setRole(Role.ROLE_USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPoint(0L);
        authRepository.save(user);

        return UserRegisterResult.SUCCESS;
    }
}
