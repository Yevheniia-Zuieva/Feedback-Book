package sumdu.edu.ua.web.controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.web.dto.CommentRequest;

public class CommentsController {

    private final CommentRepositoryPort commentRepo;

    public CommentsController(CommentRepositoryPort commentRepo) {
        this.commentRepo = commentRepo;
    }

    public void registerRoutes(Javalin app) {
        app.get("/api/comments", this::getComments);
        app.post("/api/comments", this::addComment);
        app.delete("/api/comments/{id}", this::deleteComment);
    }

    private void getComments(Context ctx) {
        long bookId = Long.parseLong(ctx.queryParamAsClass("bookId", String.class).get());
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(0);
        int size = ctx.queryParamAsClass("size", Integer.class).getOrDefault(20);

        var comments = commentRepo.list(bookId, null, null, new PageRequest(page, size));
        ctx.json(comments);
    }

    private void addComment(Context ctx) {
        CommentRequest req = ctx.bodyAsClass(CommentRequest.class);

        if (req.author() == null || req.author().isBlank() ||
            req.text() == null || req.text().isBlank()) {
            ctx.status(400).json("Author and text are required");
            return;
        }

        commentRepo.add(req.bookId(), req.author().trim(), req.text().trim());
        ctx.status(201);
    }

    private void deleteComment(Context ctx) {
        long commentId = Long.parseLong(ctx.pathParam("id"));
        long bookId = Long.parseLong(ctx.queryParamAsClass("bookId", String.class).get());

        commentRepo.delete(bookId, commentId);
        ctx.status(204);
    }
}