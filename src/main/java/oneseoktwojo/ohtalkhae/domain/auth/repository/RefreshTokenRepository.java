package oneseoktwojo.ohtalkhae.domain.auth.repository;

import oneseoktwojo.ohtalkhae.domain.auth.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
}
