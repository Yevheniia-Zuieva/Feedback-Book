package sumdu.edu.ua.persistence.adapter;

import org.springframework.stereotype.Component;
import sumdu.edu.ua.core.domain.CommentEntity;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.persistence.repository.CommentRepository;

import java.util.List;

@Component
public class JpaCommentRepositoryAdapter implements CommentRepositoryPort {

    private final CommentRepository commentRepository;

    public JpaCommentRepositoryAdapter(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void delete(long bookId, long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public void addComment(long bookId, long userId, String content) {
        // Реалізація збереження коментаря
    }

    @Override
    public List<CommentEntity> findByBookId(long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    @Override
    public List<CommentEntity> findByUserId(long userId) {
        return commentRepository.findByUserId(userId);
    }
}