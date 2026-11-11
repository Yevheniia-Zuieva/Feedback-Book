package sumdu.edu.ua.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.core.domain.CommentEntity;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;
import sumdu.edu.ua.core.exceptions.InvalidCommentTextException;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    public void delete(long bookId, long commentId, Instant createdAt) {
        if (bookId <= 0 || commentId <= 0) {
            log.warn("Invalid delete request: bookId={}, commentId={}", bookId, commentId);
            throw new InvalidCommentDeleteException("Некоректний ідентифікатор книги або коментаря");
        }

        if (createdAt == null) {
            log.warn("Invalid delete request: createdAt is null for commentId={}", commentId);
            throw new InvalidCommentDeleteException("Дата створення коментаря обов'язкова");
        }

        Instant limit = Instant.now().minus(24, ChronoUnit.HOURS);
        if (createdAt.isBefore(limit)) {
            log.warn("Comment delete rejected: commentId={} created at {} is older than 24h", commentId, createdAt);
            throw new CommentTooOldException("Коментар створено більше ніж 24 години тому і не може бути видалений");
        }

        repo.delete(bookId, commentId);
    }

    public void addComment(long bookId, long userId, String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidCommentTextException("Текст коментаря не може бути порожнім");
        }
        if (content.trim().length() < 3 || content.length() > 500) {
            throw new InvalidCommentTextException("Довжина коментаря повинна бути від 3 до 500 символів");
        }
        if (content.toLowerCase().contains("spam")) {
            throw new InvalidCommentTextException("Коментар містить заборонені слова");
        }
        repo.addComment(bookId, userId, content);
    }

    public List<CommentEntity> getCommentsByBookId(long bookId) {
        return repo.findByBookId(bookId);
    }

    public List<CommentEntity> getCommentsByUser(long userId) {
        return repo.findByUserId(userId);
    }
}