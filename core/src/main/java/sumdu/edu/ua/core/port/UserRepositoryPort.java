package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.UserEntity;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<UserEntity> findById(Long id);
}
