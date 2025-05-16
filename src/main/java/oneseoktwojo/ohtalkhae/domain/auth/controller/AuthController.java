package oneseoktwojo.ohtalkhae.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.dto.TokenRefreshRequest;
import oneseoktwojo.ohtalkhae.domain.auth.dto.TokenResponse;
import oneseoktwojo.ohtalkhae.domain.auth.jwt.JWTUtil;
import oneseoktwojo.ohtalkhae.domain.auth.service.UserService;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.auth.enums.UserRegisterResult;
import oneseoktwojo.ohtalkhae.domain.auth.service.RefreshTokenService;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final JWTUtil jwtUtil;

    @Value("${spring.jwt.access-token-expiration-time}")
    private Long accessTokenExpirationTime; // 1 hour


    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResult result = userService.register(request);
        return createRegisterResponse(result);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String accessToken = request.getAccessToken();
        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyRefreshToken)
                .map(refreshTokenService::refreshRefreshToken)
                .map(newRefreshToken -> {
                    String role = jwtUtil.getRole(accessToken);
                    String newAccessToken = jwtUtil.createJwt(newRefreshToken.getUsername(), role, accessTokenExpirationTime);
                    return ApiResponse.success(HttpStatus.OK, new TokenResponse(newAccessToken, newRefreshToken.getToken()));
                })
                .orElseThrow(() -> new RuntimeException("Invalid refresh token."));
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<?>> authTest() {
        return ApiResponse.success(HttpStatus.OK, "Authentication test successful.");
    }

    private ResponseEntity<ApiResponse<?>> createRegisterResponse(UserRegisterResult result) {
        return switch (result) {
            case SUCCESS -> ApiResponse.success(HttpStatus.OK, "User registered successfully.");
            case DUPLICATED_USERNAME -> ApiResponse.error(HttpStatus.BAD_REQUEST, "Duplicated username.");
            case DUPLICATED_EMAIL -> ApiResponse.error(HttpStatus.BAD_REQUEST, "Duplicated email.");
            case DUPLICATED_PHONE -> ApiResponse.error(HttpStatus.BAD_REQUEST, "Duplicated phone number.");
            default -> ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error occurred.");
        };
    }
}
