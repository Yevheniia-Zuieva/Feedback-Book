package sumdu.edu.ua.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.service.MailService;

@Controller
@RequestMapping("/books")
public class BooksController {

    private final CatalogRepositoryPort bookRepo;
    private final MailService mailService;

    public BooksController(CatalogRepositoryPort bookRepo, MailService mailService) {
        this.bookRepo = bookRepo;
        this.mailService = mailService;
    }

    @GetMapping
    public String getAllBooks(Model model) {
        var books = bookRepo.search(null, new PageRequest(0, 20)).getItems();
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("bookForm", new Book());
        return "book-form";
    }

    @PostMapping
    public String addBook(@ModelAttribute("bookForm") Book book) {
        Book savedBook = bookRepo.add(book.getTitle(), book.getAuthor(), book.getPubYear());
        mailService.sendNewBookEmail(savedBook != null ? savedBook : book);
        return "redirect:/books";
    }
}