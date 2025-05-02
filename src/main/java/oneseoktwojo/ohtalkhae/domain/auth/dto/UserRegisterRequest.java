package oneseoktwojo.ohtalkhae.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Data
public class UserRegisterRequest {
    @NotNull(message = "Username is required.")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Invalid username.")
    @Size(min = 4, max = 20)
    private String username;
    @NotNull(message = "Password is required.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).*$", message = "Must contain at least one uppercase letter, lowercase letter, number, and special character.")
    @Size(min = 8, max = 32)
    private String password;
    @NotNull(message = "Phone number is required.")
    @Pattern(regexp = "^\\d{3}-\\d{4}-\\d{4}$", message = "Invalid phone number.")
    private String phone;
    @NotNull(message = "Email is required.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format.")
    @Size(min = 6, max = 100) // a@b.cd 만 해도 6자리이므로 6자리 아래는 잘못된 입력
    private String email;
    @NotNull(message = "Birthday is required.")
    private LocalDate birthday;
}
