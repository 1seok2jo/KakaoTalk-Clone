package oneseoktwojo.ohtalkhae.domain.auth;

import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "auth")
public class AuthController {
    @PostMapping("/register")
    public ApiResponse<?> register(UserRegisterRequest request) {

        return ApiResponse.error(500, "Not Implemented");
    }
}
