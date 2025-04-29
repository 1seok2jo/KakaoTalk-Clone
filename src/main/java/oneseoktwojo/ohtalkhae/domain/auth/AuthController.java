package oneseoktwojo.ohtalkhae.domain.auth;

import jakarta.validation.Valid;
import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.global.dto.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody UserRegisterRequest request) {

        return ApiResponse.error(500, "Not Implemented");
    }
}
