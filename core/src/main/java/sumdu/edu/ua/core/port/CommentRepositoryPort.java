package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.CommentEntity;
import java.util.List;

public interface CommentRepositoryPort {
    List<CommentEntity> findByBookId(Long bookId);
    CommentEntity add(Long bookId, String text, String author);
    boolean delete(Long commentId, Long bookId);
    List<CommentEntity> findByUserId(Long userId);
}