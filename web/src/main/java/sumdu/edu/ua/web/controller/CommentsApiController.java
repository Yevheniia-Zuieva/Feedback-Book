package sumdu.edu.ua.web.controller;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentsApiController {

    private static final Logger log = LoggerFactory.getLogger(CommentsApiController.class);
    private final CommentRepositoryPort commentRepo;
    private final CommentService commentService;

    public CommentsApiController(CommentRepositoryPort commentRepo, CommentService commentService) {
        this.commentRepo = commentRepo;
        this.commentService = commentService;
    }

    // GET /api/comments?bookId=1&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<Comment>> getComments(
            @RequestParam(name = "bookId", defaultValue = "0") long bookId,
            @RequestParam(name = "author", required = false) String author,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
            PageRequest pageRequest = new PageRequest(page, size);
            Page<Comment> result = commentRepo.list(bookId, author, null, pageRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Помилка доступу до БД при виконанні GET /api/comments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/comments
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentDto dto) {
        if (dto.getAuthor() == null || dto.getAuthor().isBlank()
                || dto.getText() == null || dto.getText().isBlank()) {
            return ResponseEntity.badRequest().body("author & text required");
        }

        try {
            commentRepo.add(dto.getBookId(), dto.getAuthor().trim(), dto.getText().trim());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Помилка при додаванні коментаря", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DELETE /api/comments/{id}?bookId=1
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable("id") long id, 
            @RequestParam("bookId") long bookId) {
        try {
            // Передаємо потрібні параметри у сервіс
            commentService.deleteComment(bookId, id, Instant.now()); 
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) { // Перехоплюємо IllegalStateException
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Помилка при видаленні коментаря з ID {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // DTO клас для прийому даних коментаря з тіла JSON-запиту
    public static class CommentDto {
        private long bookId;
        private String author;
        private String text;

        public long getBookId() { return bookId; }
        public void setBookId(long bookId) { this.bookId = bookId; }
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
    }
}