package oneseoktwojo.ohtalkhae.domain.auth;

import jakarta.transaction.Transactional;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterResult;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Transactional
    public UserRegisterResult Register(UserRegisterRequest request) {

        User user = new User(
                null,
                request.getUsername(),
                request.getPassword(),
                request.getPhone(),
                request.getEmail(),
                request.getBirthday(),
                0L,
                Role.ROLE_USER.toString(),
                null);
        return UserRegisterResult.UNKNOWN_ERROR;
    }
}
