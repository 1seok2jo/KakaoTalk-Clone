package oneseoktwojo.ohtalkhae.domain.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.auth.enums.UserRegisterResult;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResult result = authService.register(request);
        return createRegisterResponse(result);
    }

    @GetMapping("/test")
    public ApiResponse<?> authTest() {
        return ApiResponse.success(200, "Authentication test successful.");
    }

    private ApiResponse<?> createRegisterResponse(UserRegisterResult result) {
        return switch (result) {
            case SUCCESS -> ApiResponse.success(200, "User registered successfully.");
            case DUPLICATED_USERNAME -> ApiResponse.error(400, "Duplicated username.");
            case DUPLICATED_EMAIL -> ApiResponse.error(400, "Duplicated email.");
            case DUPLICATED_PHONE -> ApiResponse.error(400, "Duplicated phone number.");
            default -> ApiResponse.error(500, "Unknown error occurred.");
        };
    }
}
