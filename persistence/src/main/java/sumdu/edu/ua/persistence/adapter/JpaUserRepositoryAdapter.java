package sumdu.edu.ua.persistence.adapter;

import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.UserEntity;
import sumdu.edu.ua.core.port.UserRepositoryPort;
import sumdu.edu.ua.persistence.repository.UserRepository;

import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final UserRepository userRepository;

    public JpaUserRepositoryAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<UserEntity> findByVerificationToken(String token) {
        return userRepository.findByVerificationToken(token);
    }

    @Override
    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }
}