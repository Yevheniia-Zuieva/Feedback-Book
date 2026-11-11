package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.CommentEntity;
import java.util.List;

public interface CommentRepositoryPort {
    void delete(long bookId, long commentId);
    void addComment(long bookId, long userId, String content);
    List<CommentEntity> findByBookId(long bookId);
    List<CommentEntity> findByUserId(long userId);
}