package sumdu.edu.ua.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sumdu.edu.ua.db.CommentDao;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class CommentsServlet extends HttpServlet {
    private final CommentDao dao = new CommentDao();
    private final ObjectMapper om = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(CommentsServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/json; charset=UTF-8");
            var list = dao.list();
            om.writeValue(resp.getWriter(), list);
        } catch (SQLException e) {
            log.error("DB error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        String author = req.getParameter("author");
        String text = req.getParameter("text");

        if (author == null || author.trim().isEmpty() || author.length() > 64 ||
            text == null || text.trim().isEmpty() || text.length() > 1000) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Validation error: invalid author or text");
            return;
        }

        try {
            dao.add(author.trim(), text.trim());
            log.info("New comment added. Author: {}, Text length: {}", author.trim(), text.trim().length());
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
        } catch (SQLException e) {
            log.error("DB error", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "DB error");
        }
    }
}