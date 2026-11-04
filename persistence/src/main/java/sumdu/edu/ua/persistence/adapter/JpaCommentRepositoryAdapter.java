package sumdu.edu.ua.persistence.adapter;

import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.BookEntity;
import sumdu.edu.ua.core.domain.CommentEntity;
import sumdu.edu.ua.core.domain.UserEntity;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.persistence.repository.CommentRepository;
import sumdu.edu.ua.persistence.repository.UserRepository;

import java.util.List;

@Repository
public class JpaCommentRepositoryAdapter implements CommentRepositoryPort {

    private final CommentRepository commentRepo;
    private final CatalogRepositoryPort catalogRepo;
    private final UserRepository userRepo;

    public JpaCommentRepositoryAdapter(CommentRepository commentRepo, 
                                       CatalogRepositoryPort catalogRepo,
                                       UserRepository userRepo) {
        this.commentRepo = commentRepo;
        this.catalogRepo = catalogRepo;
        this.userRepo = userRepo;
    }

    @Override
    public List<CommentEntity> findByBookId(Long bookId) {
        return commentRepo.findByBookId(bookId);
    }

    @Override
    public CommentEntity add(Long bookId, String text, String author) {
        BookEntity book = catalogRepo.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        // Шукаємо існуючого користувача за username, якщо не знайдено — створюємо нового користувача
        UserEntity user = userRepo.findByUsername(author)
                .orElseGet(() -> userRepo.save(new UserEntity(null, author, "pass123", "USER")));

        CommentEntity comment = new CommentEntity(null, text, book, user);
        return commentRepo.save(comment);
    }

    @Override
    public boolean delete(Long commentId, Long bookId) {
        if (commentRepo.existsById(commentId)) {
            commentRepo.deleteById(commentId);
            return true;
        }
        return false;
    }

    @Override
    public List<CommentEntity> findByUserId(Long userId) {
        return commentRepo.findByUserId(userId);
    }
}