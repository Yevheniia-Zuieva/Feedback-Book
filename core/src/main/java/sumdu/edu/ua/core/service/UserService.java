package sumdu.edu.ua.core.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumdu.edu.ua.core.domain.UserEntity;
import sumdu.edu.ua.core.dto.RegisterUserDto;
import sumdu.edu.ua.core.port.MailPort;
import sumdu.edu.ua.core.port.UserRepositoryPort;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepositoryPort users;
    private final PasswordEncoder passwordEncoder;
    private final MailPort mailPort;

    public UserService(UserRepositoryPort users, PasswordEncoder passwordEncoder, MailPort mailPort) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.mailPort = mailPort;
    }

    public UserEntity findById(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    public UserEntity findByEmailOrThrow(String email) {
        return users.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
    }

    @Transactional
    public void register(RegisterUserDto dto) {
        if (users.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Користувач з таким email вже існує");
        }

        UserEntity u = new UserEntity();
        u.setEmail(dto.getEmail());
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setNickname(dto.getNickname() != null && !dto.getNickname().isBlank() ? dto.getNickname() : dto.getEmail());
        u.setRole("REGISTERED");
        u.setVerified(false);

        String token = UUID.randomUUID().toString();
        u.setVerificationToken(token);

        users.save(u);
        mailPort.sendVerificationEmail(u.getEmail(), token);
    }

    @Transactional
    public boolean verifyAccount(String token) {
        Optional<UserEntity> opt = users.findByVerificationToken(token);
        if (opt.isEmpty()) return false;

        UserEntity u = opt.get();
        u.setVerified(true);
        u.setRole("USER");
        u.setVerificationToken(null);

        users.save(u);
        return true;
    }
}