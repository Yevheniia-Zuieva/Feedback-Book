package sumdu.edu.ua.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumdu.edu.ua.core.domain.UserEntity;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByVerificationToken(String verificationToken);
}