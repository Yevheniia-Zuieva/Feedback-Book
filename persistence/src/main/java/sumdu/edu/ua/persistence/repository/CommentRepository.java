package sumdu.edu.ua.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumdu.edu.ua.core.domain.CommentEntity;
import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByUserId(Long userId);
    List<CommentEntity> findByBookId(Long bookId);
}