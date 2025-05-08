package oneseoktwojo.ohtalkhae.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.dto.TokenRefreshRequest;
import oneseoktwojo.ohtalkhae.domain.auth.dto.TokenResponse;
import oneseoktwojo.ohtalkhae.domain.auth.jwt.JWTUtil;
import oneseoktwojo.ohtalkhae.domain.auth.service.AuthService;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.auth.enums.UserRegisterResult;
import oneseoktwojo.ohtalkhae.domain.auth.service.RefreshTokenService;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    @Value("${spring.jwt.access-token-expiration-time}")
    private Long accessTokenExpirationTime; // 1 hour


    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResult result = authService.register(request);
        return createRegisterResponse(result);
    }

    @PostMapping("/refresh")
    public ApiResponse<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String accessToken = request.getAccessToken();
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyRefreshToken)
                .map(refreshTokenService::refreshRefreshToken)
                .map(newRefreshToken -> {
                    String role = jwtUtil.getRole(accessToken);
                    String newAccessToken = jwtUtil.createJwt(newRefreshToken.getUsername(), role, accessTokenExpirationTime);
                    return ApiResponse.success(200, new TokenResponse(newAccessToken, newRefreshToken.getToken()));
                })
                .orElseThrow(() -> new RuntimeException("Invalid refresh token."));
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
