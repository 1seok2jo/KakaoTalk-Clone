package oneseoktwojo.ohtalkhae.domain.auth;

import jakarta.validation.Valid;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.auth.enums.UserRegisterResult;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResult result = authService.register(request);
        if (result == UserRegisterResult.SUCCESS) {
            return ApiResponse.success(200, "User registered successfully.");
        } else if (result == UserRegisterResult.DUPLICATED_USERNAME) {
            return ApiResponse.error(400, "Duplicated username.");
        } else if (result == UserRegisterResult.DUPLICATED_EMAIL) {
            return ApiResponse.error(400, "Duplicated email.");
        } else if (result == UserRegisterResult.DUPLICATED_PHONE) {
            return ApiResponse.error(400, "Duplicated phone number.");
        } else {
            return ApiResponse.error(500, "Unknown error occurred.");
        }
    }
}
