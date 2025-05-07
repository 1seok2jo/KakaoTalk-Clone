package oneseoktwojo.ohtalkhae.domain.profile;

import oneseoktwojo.ohtalkhae.domain.profile.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
