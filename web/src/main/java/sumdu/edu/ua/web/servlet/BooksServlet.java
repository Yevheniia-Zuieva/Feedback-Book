package sumdu.edu.ua.web.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.core.domain.Book;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;

import java.io.IOException;
import java.util.List;

public class BooksServlet extends HttpServlet {

    private final CatalogRepositoryPort bookRepo;

    public BooksServlet(CatalogRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    @Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    resp.setContentType("application/json; charset=UTF-8");
    
    // Отримуємо додатковий шлях після /books 
    String pathInfo = req.getPathInfo(); 

    if (pathInfo != null && pathInfo.length() > 1) {
        try {
            // Витягуємо ID з рядка 
            long bookId = Long.parseLong(pathInfo.substring(1));
            
            // Шукаємо книгу за ID 
            Book book = bookRepo.findById(bookId); // або bookRepo.get(bookId)

            if (book != null) {
                // Повертаємо одну книгу у форматі JSON
                String json = String.format("{\"id\":%d, \"title\":\"%s\", \"author\":\"%s\", \"pubYear\":%d}",
                        book.getId(), book.getTitle(), book.getAuthor(), book.getPubYear());
                resp.getWriter().write(json);
                return;
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Book not found\"}");
                return;
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"Invalid book ID format\"}");
            return;
        }
    }

    // Якщо шлях порожній (/books або /books/), повертаємо весь список 
    PageRequest pageRequest = new PageRequest(0, 50);
    List<Book> books = bookRepo.search(null, pageRequest).getItems();

    StringBuilder json = new StringBuilder("[");
    for (int i = 0; i < books.size(); i++) {
        Book b = books.get(i);
        json.append("{\"id\":").append(b.getId())
            .append(", \"title\":\"").append(b.getTitle())
            .append("\", \"author\":\"").append(b.getAuthor())
            .append("\", \"pubYear\":").append(b.getPubYear()).append("}");
        if (i < books.size() - 1) json.append(",");
    }
    json.append("]");

    resp.getWriter().write(json.toString());
}
}