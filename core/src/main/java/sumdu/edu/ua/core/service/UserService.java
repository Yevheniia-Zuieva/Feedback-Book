package sumdu.edu.ua.core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumdu.edu.ua.core.domain.UserEntity;
import sumdu.edu.ua.core.port.UserRepositoryPort;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepositoryPort userRepositoryPort;

    public UserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public UserEntity findById(Long id) {
        return userRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }
}