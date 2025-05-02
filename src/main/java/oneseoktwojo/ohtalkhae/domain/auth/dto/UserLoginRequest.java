package oneseoktwojo.ohtalkhae.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class UserLoginRequest {
    @NotNull(message = "Username is required.")
    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Invalid username.")
    @Size(min = 4, max = 20)
    private String username;
    @NotNull(message = "Password is required.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*(),.?\":{}|<>]).*$", message = "Must contain at least one uppercase letter, lowercase letter, number, and special character.")
    @Size(min = 8, max = 32)
    private String password;
}
