package oneseoktwojo.ohtalkhae.domain.auth.service;

import lombok.RequiredArgsConstructor;
import oneseoktwojo.ohtalkhae.domain.auth.dto.CustomUserDetails;
import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import oneseoktwojo.ohtalkhae.domain.auth.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user != null) {
            return new CustomUserDetails(user);
        }

        throw new UsernameNotFoundException("User with username '" + username + "' not found.");
    }
}
