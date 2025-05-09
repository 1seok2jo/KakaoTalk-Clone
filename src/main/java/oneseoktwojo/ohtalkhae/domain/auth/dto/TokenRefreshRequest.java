package oneseoktwojo.ohtalkhae.domain.auth.dto;

import lombok.*;

@Data
public class TokenRefreshRequest {
    private String refreshToken;
    private String accessToken;
}
