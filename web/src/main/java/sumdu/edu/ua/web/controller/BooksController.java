package sumdu.edu.ua.web.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.web.dto.BookRequest;

public class BooksController {

    private final CatalogRepositoryPort bookRepo;

    public BooksController(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/books", this::getBooks);
        app.get("/api/books/{id}", this::getBookById);
        app.post("/api/books", this::addBook);
    }

    private void getBooks(Context ctx) {
        String q = ctx.queryParam("q");
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(20);

        var result = bookRepo.search(q, new PageRequest(page, size));
        ctx.json(result);
    }

    private void getBookById(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        var book = bookRepo.findById(id);

        if (book == null) {
            ctx.status(404).json("Book not found");
            return;
        }

        ctx.json(book);
    }

    private void addBook(Context ctx) {
        BookRequest req = ctx.bodyAsClass(BookRequest.class);

        if (req.title() == null || req.title().isBlank() ||
            req.author() == null || req.author().isBlank() || req.pubYear() <= 0) {
            ctx.status(400).json("Invalid book data");
            return;
        }

        var saved = bookRepo.add(req.title().trim(), req.author().trim(), req.pubYear());
        ctx.status(201).json(saved);
    }
}