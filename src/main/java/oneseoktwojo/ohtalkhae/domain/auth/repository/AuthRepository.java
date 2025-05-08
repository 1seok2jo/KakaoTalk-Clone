package oneseoktwojo.ohtalkhae.domain.auth.repository;

import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    User findByUsername(String username);
}
