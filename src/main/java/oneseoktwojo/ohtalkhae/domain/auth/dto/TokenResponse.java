package oneseoktwojo.ohtalkhae.domain.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
}
