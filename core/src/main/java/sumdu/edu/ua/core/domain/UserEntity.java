package sumdu.edu.ua.core.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 128)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "nickname", length = 128)
    private String nickname;

    @Column(name = "role", length = 32)
    private String role;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "verification_token", length = 255)
    private String verificationToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentEntity> comments = new ArrayList<>();

    public String getUsername() {
        return (nickname != null && !nickname.isBlank()) ? nickname : email;
    }
}