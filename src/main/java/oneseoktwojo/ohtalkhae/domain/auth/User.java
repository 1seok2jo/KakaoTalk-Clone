package oneseoktwojo.ohtalkhae.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import oneseoktwojo.ohtalkhae.domain.auth.enums.Role;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String phone;
    @Column(unique = true)
    private String email;
    private LocalDate birthday;
    private Long point;
    @Enumerated(EnumType.STRING)
    private Role role;
    @CreatedDate
    private LocalDateTime createdAt;
}