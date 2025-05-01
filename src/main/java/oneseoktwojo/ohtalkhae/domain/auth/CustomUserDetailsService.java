package oneseoktwojo.ohtalkhae.domain.auth;

import oneseoktwojo.ohtalkhae.domain.auth.dto.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public CustomUserDetailsService(AuthRepository authRepository, BCryptPasswordEncoder passwordEncoder) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = authRepository.findByUsername(username);

        if (user != null && user.getPassword() != null && passwordEncoder.matches(user.getPassword(), user.getPassword())) {
            return new CustomUserDetails(user);
        }

        return null;
    }
}
