package sumdu.edu.ua.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.CommentEntity;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentsController {

    private final CommentRepositoryPort commentRepo;
    private final CatalogRepositoryPort bookRepo;

    public CommentsController(CommentRepositoryPort commentRepo, CatalogRepositoryPort bookRepo) {
        this.commentRepo = commentRepo;
        this.bookRepo = bookRepo;
    }

    @GetMapping
    public String list(@RequestParam("bookId") long bookId, Model model) {
        var book = bookRepo.findById(bookId).orElse(null);
        if (book == null) {
            return "redirect:/books";
        }

        List<CommentEntity> comments = commentRepo.findByBookId(bookId);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "book-comments";
    }

    @PostMapping
    public String addComment(@RequestParam("bookId") long bookId,
                             @RequestParam("author") String author,
                             @RequestParam("text") String text) {
        commentRepo.add(bookId, text, author);
        return "redirect:/comments?bookId=" + bookId;
    }

    @PostMapping("/delete")
    public String deleteComment(@RequestParam("commentId") long commentId,
                                @RequestParam("bookId") long bookId) {
        commentRepo.delete(commentId, bookId);
        return "redirect:/comments?bookId=" + bookId;
    }
}