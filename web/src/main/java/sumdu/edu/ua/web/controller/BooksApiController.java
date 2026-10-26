package sumdu.edu.ua.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

@RestController
@RequestMapping("/api/books")
public class BooksApiController {

    private static final Logger log = LoggerFactory.getLogger(BooksApiController.class);
    private final CatalogRepositoryPort bookRepo;

    // Впровадження залежності через конструктор (DI)
    public BooksApiController(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    // GET /api/books?q=...&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<Book>> getBooks(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        try {
            PageRequest pageRequest = new PageRequest(page, size);
            Page<Book> result = bookRepo.search(q, pageRequest);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Помилка доступу до БД при виконанні GET /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // GET /api/books/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable("id") long id) {
        try {
            Book book = bookRepo.findById(id);
            if (book == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }
            return ResponseEntity.ok(book);
        } catch (Exception e) {
            log.error("Помилка доступу до БД при виконанні GET /api/books/{}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // POST /api/books
    @PostMapping
    public ResponseEntity<?> addBook(@RequestBody Book book) {
        if (book.getTitle() == null || book.getTitle().isBlank()
                || book.getAuthor() == null || book.getAuthor().isBlank()) {
            return ResponseEntity.badRequest().body("title & author required");
        }
        if (book.getPubYear() <= 0) {
            return ResponseEntity.badRequest().body("invalid pubYear");
        }

        try {
            Book saved = bookRepo.add(
                    book.getTitle().trim(),
                    book.getAuthor().trim(),
                    book.getPubYear()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Помилка доступу до БД при виконанні POST /api/books", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}