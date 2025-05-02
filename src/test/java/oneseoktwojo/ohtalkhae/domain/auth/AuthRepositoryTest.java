package oneseoktwojo.ohtalkhae.domain.auth;

import oneseoktwojo.ohtalkhae.domain.auth.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jwt.secret=testsetsetsetsetsetsetsetset")
class AuthRepositoryTest {
    @Autowired
    private AuthRepository authRepository;

    @Test
    void existsByUsername() {
        // Given
        String username = "testuser";
        User user = new User(
                null,
                username,
                "testpassword",
                "000-0000-0000",
                "test@test.com",
                LocalDate.now(),
                0L,
                Role.ROLE_USER,
                null);
        authRepository.save(user);

        // When
        Boolean exists = authRepository.existsByUsername(username);

        // Then
        assertTrue(exists);

        // Clean up
        authRepository.delete(user);
    }

    @Test
    void existsByEmail() {
        // Given
        String email = "test@test.com";
        User user = new User(
                null,
                "testuser",
                "testpassword",
                "000-0000-0000",
                email,
                LocalDate.now(),
                0L,
                Role.ROLE_USER,
                null);

        authRepository.save(user);

        // When
        Boolean exists = authRepository.existsByEmail(email);

        // Then
        assertTrue(exists);

        // Clean up
        authRepository.delete(user);
    }

    @Test
    void existsByPhone() {
        // Given
        String phone = "000-0000-0000";
        User user = new User(
                null,
                "testuser",
                "testpassword",
                phone,
                "test@test.com",
                LocalDate.now(),
                0L,
                Role.ROLE_USER,
                null);

        authRepository.save(user);
        // When
        Boolean exists = authRepository.existsByPhone(phone);

        // Then
        assertTrue(exists);

        // Clean up
        authRepository.delete(user);
    }

    @Test
    void findByUsername() {
        // Given
        String username = "testuser";
        User user = new User(
                null,
                username,
                "testpassword",
                "000-0000-0000",
                "test@test.com",
                LocalDate.now(),
                0L,
                Role.ROLE_USER,
                null);
        authRepository.save(user);

        // When
        User foundUser = authRepository.findByUsername(username);

        // Then
        assertNotNull(foundUser);

        // Clean up
        authRepository.delete(user);
    }
}