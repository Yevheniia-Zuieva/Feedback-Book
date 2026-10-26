package sumdu.edu.ua.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

@Controller
@RequestMapping("/comments")
public class CommentsController {

    private final CommentRepositoryPort commentRepo;
    private final CatalogRepositoryPort bookRepo;

    public CommentsController(CommentRepositoryPort commentRepo,
                               CatalogRepositoryPort bookRepo) {
        this.commentRepo = commentRepo;
        this.bookRepo = bookRepo;
    }

    @GetMapping
    public String list(@RequestParam("bookId") long bookId, Model model) {
        var book = bookRepo.findById(bookId);
        if (book == null) return "redirect:/books";

        var comments = commentRepo
                .list(bookId, null, null, new PageRequest(0, 20))
                .getItems();

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        return "book-comments";
    }

    @PostMapping
    public String add(@RequestParam("bookId") long bookId,
                      @RequestParam("author") String author,
                      @RequestParam("text") String text) {
        commentRepo.add(bookId, author.trim(), text.trim());
        return "redirect:/comments?bookId=" + bookId;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("bookId") long bookId,
                         @RequestParam("commentId") long commentId) {
        commentRepo.delete(bookId, commentId);
        return "redirect:/comments?bookId=" + bookId;
    }
}