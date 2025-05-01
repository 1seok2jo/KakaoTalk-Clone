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
        return createRegisterResponse(result);
    }

    private ApiResponse<?> createRegisterResponse(UserRegisterResult result) {
        switch (result) {
            case SUCCESS:
                return ApiResponse.success(200, "User registered successfully.");
            case DUPLICATED_USERNAME:
                return ApiResponse.error(400, "Duplicated username.");
            case DUPLICATED_EMAIL:
                return ApiResponse.error(400, "Duplicated email.");
            case DUPLICATED_PHONE:
                return ApiResponse.error(400, "Duplicated phone number.");
            default:
                return ApiResponse.error(500, "Unknown error occurred.");
        }
    }
}
