package sumdu.edu.ua.web.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.service.CommentService;

import java.time.Instant;

@Controller
@RequestMapping("/comments")
public class CommentsController {

    private final CommentService commentService;
    private final CatalogRepositoryPort catalogRepositoryPort;

    public CommentsController(CommentService commentService, CatalogRepositoryPort catalogRepositoryPort) {
        this.commentService = commentService;
        this.catalogRepositoryPort = catalogRepositoryPort;
    }

    @GetMapping
    public String getComments(@RequestParam("bookId") long bookId, Model model) {
        model.addAttribute("comments", commentService.getCommentsByBookId(bookId));
        model.addAttribute("bookId", bookId);
        
        // Знаходимо книгу та передаємо її в модель для шаблону Thymeleaf
        catalogRepositoryPort.findById(bookId).ifPresent(book -> model.addAttribute("book", book));
        
        return "book-comments";
    }

    @PostMapping
    public String addComment(
            @RequestParam("bookId") long bookId,
            @RequestParam("userId") long userId,
            @RequestParam("content") String content) {
        commentService.addComment(bookId, userId, content);
        return "redirect:/comments?bookId=" + bookId;
    }

    @PostMapping("/delete")
    public String delete(
            @RequestParam("bookId") long bookId,
            @RequestParam("commentId") long commentId,
            @RequestParam("createdAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant createdAt) {

        commentService.delete(bookId, commentId, createdAt);
        return "redirect:/comments?bookId=" + bookId;
    }
}