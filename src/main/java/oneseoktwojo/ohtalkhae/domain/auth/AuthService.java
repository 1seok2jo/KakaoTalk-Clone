package oneseoktwojo.ohtalkhae.domain.auth;

import jakarta.transaction.Transactional;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.auth.enums.Role;
import oneseoktwojo.ohtalkhae.domain.auth.enums.UserRegisterResult;
import oneseoktwojo.ohtalkhae.domain.auth.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;

    public AuthService(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

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
        user.setRole(Role.ROLE_USER.toString());
        user.setPoint(0L);
        authRepository.save(user);

        return UserRegisterResult.SUCCESS;
    }
}
