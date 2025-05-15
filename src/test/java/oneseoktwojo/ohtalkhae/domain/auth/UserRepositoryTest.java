package oneseoktwojo.ohtalkhae.domain.auth;

import oneseoktwojo.ohtalkhae.domain.auth.entity.User;
import oneseoktwojo.ohtalkhae.domain.auth.enums.Role;
import oneseoktwojo.ohtalkhae.domain.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jwt.secret=testsetsetsetsetsetsetsetset")
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByUsername() {
        // Given
        String username = "testuser";

        User user = User.builder()
                .username(username)
                .password("testpassword")
                .phone("000-0000-0000")
                .email("test@test.com")
                .birthday(LocalDate.now())
                .point(0L)
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        // When
        Boolean exists = userRepository.existsByUsername(username);

        // Then
        assertTrue(exists);

        // Clean up
        userRepository.delete(user);
    }

    @Test
    void existsByEmail() {
        // Given
        String email = "test@test.com";

        User user = User.builder()
                .username("testuser")
                .password("testpassword")
                .phone("000-0000-0000")
                .email(email)
                .birthday(LocalDate.now())
                .point(0L)
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);

        // When
        Boolean exists = userRepository.existsByEmail(email);

        // Then
        assertTrue(exists);

        // Clean up
        userRepository.delete(user);
    }

    @Test
    void existsByPhone() {
        // Given
        String phone = "000-0000-0000";

        User user = User.builder()
                .username("testuser")
                .password("testpassword")
                .phone(phone)
                .email("test@testcom")
                .birthday(LocalDate.now())
                .point(0L)
                .role(Role.ROLE_USER)
                .build();

        userRepository.save(user);
        // When
        Boolean exists = userRepository.existsByPhone(phone);

        // Then
        assertTrue(exists);

        // Clean up
        userRepository.delete(user);
    }

    @Test
    void findByUsername() {
        // Given
        String username = "testuser";
        User user = User.builder()
                .username(username)
                .password("testpassword")
                .phone("000-0000-0000")
                .email("test@testcom")
                .birthday(LocalDate.now())
                .point(0L)
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);

        // When
        User foundUser = userRepository.findByUsername(username);

        // Then
        assertNotNull(foundUser);

        // Clean up
        userRepository.delete(user);
    }
}