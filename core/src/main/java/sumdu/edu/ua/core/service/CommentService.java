package sumdu.edu.ua.core.service;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumdu.edu.ua.core.domain.CommentEntity;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepositoryPort commentRepo;

    public CommentService(CommentRepositoryPort commentRepo) {
        this.commentRepo = commentRepo;
    }

    public List<CommentEntity> getCommentsByBook(Long bookId) {
        return commentRepo.findByBookId(bookId);
    }

    public List<CommentEntity> getCommentsByUser(Long userId) {
        return commentRepo.findByUserId(userId);
    }

    @Transactional
    public CommentEntity addComment(Long bookId, String text, String author) {
        return commentRepo.add(bookId, text, author);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean deleteComment(Long commentId, Long bookId) {
        return commentRepo.delete(commentId, bookId);
    }
}