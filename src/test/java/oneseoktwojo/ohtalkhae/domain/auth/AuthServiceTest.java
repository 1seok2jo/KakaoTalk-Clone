package oneseoktwojo.ohtalkhae.domain.auth;

import oneseoktwojo.ohtalkhae.domain.auth.dto.UserRegisterRequest;
import oneseoktwojo.ohtalkhae.domain.auth.enums.UserRegisterResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jwt.secret=testsetsetsetsetsetsetsetset")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void register() {
        // Given
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("testuser");
        userRegisterRequest.setPassword("testpassword");
        userRegisterRequest.setPhone("000-0000-0000");
        userRegisterRequest.setEmail("test@test.com");
        userRegisterRequest.setBirthday(LocalDate.of(2000, 1, 1));

        // When
        UserRegisterResult result = authService.register(userRegisterRequest);

        // Then
        assertEquals(UserRegisterResult.SUCCESS, result);
    }

    @Test
    void registerWithDuplicatedUsername() {
        // Given
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest();
        userRegisterRequest.setUsername("testuser2");
        userRegisterRequest.setPassword("testpassword");
        userRegisterRequest.setPhone("000-0000-0001");
        userRegisterRequest.setEmail("test@test.net");
        userRegisterRequest.setBirthday(LocalDate.of(2000, 1, 1));

        // When
        authService.register(userRegisterRequest);
        UserRegisterResult result = authService.register(userRegisterRequest);

        // Then
        assertEquals(UserRegisterResult.DUPLICATED_USERNAME, result);
    }
}