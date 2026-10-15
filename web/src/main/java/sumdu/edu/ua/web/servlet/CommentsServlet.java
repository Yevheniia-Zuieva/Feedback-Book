package sumdu.edu.ua.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.Comment;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.io.IOException;
import java.util.List;

public class CommentsServlet extends HttpServlet {

    private final CatalogRepositoryPort bookRepo;
    private final CommentRepositoryPort commentRepo;

    public CommentsServlet(CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        this.bookRepo = bookRepo;
        this.commentRepo = commentRepo;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json; charset=UTF-8");
        
        // Використовуємо реальний метод list з вашого CommentRepositoryPort
        // Передаємо 0 для книги (або загальний контекст), null для фільтрів та пагінацію (наприклад, перші 50 записів)
        PageRequest pageRequest = new PageRequest(0, 50);
        Page<Comment> commentPage = commentRepo.list(0L, null, null, pageRequest);
        List<Comment> comments = commentPage.getItems();

        // Формуємо JSON-відповідь для JavaScript на фронтенді
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < comments.size(); i++) {
            Comment c = comments.get(i);
            json.append("{\"author\":\"").append(c.getAuthor())
                .append("\", \"text\":\"").append(c.getText()).append("\"}");
            if (i < comments.size() - 1) json.append(",");
        }
        json.append("]");

        resp.getWriter().write(json.toString());
    }
}