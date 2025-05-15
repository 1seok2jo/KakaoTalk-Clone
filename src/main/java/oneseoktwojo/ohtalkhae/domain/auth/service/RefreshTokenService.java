package oneseoktwojo.ohtalkhae.domain.auth.service;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.entity.RefreshToken;
import oneseoktwojo.ohtalkhae.domain.auth.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${spring.jwt.refresh-token-expiration-time}")
    private Long refreshTokenExpirationTime = 3600000L;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken generateRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .username(username)
                .token(UUID.randomUUID().toString())
                .expiredAt(Instant.now().plusMillis(refreshTokenExpirationTime))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyRefreshToken(RefreshToken refreshToken) {
        if (refreshToken == null || refreshToken.getExpiredAt().isBefore(Instant.now())) {
            return null;
        }
        return refreshToken;
    }

    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    public RefreshToken refreshRefreshToken(RefreshToken refreshToken) {
        deleteRefreshToken(refreshToken);
        return generateRefreshToken(refreshToken.getUsername());
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}
