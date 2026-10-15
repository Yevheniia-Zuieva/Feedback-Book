package sumdu.edu.ua.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import sumdu.edu.ua.web.servlet.BooksServlet;
import sumdu.edu.ua.web.servlet.CommentsServlet;

@Configuration
public class ServletConfig {

    @Bean
    public String applicationName() {
        return "Spring Boot Feedback Book App";
    }

    @Bean
    public ServletRegistrationBean<BooksServlet> booksServlet(CatalogRepositoryPort bookRepo) {
        return new ServletRegistrationBean<>(new BooksServlet(bookRepo), "/books", "/books/*");
    }

    @Bean
    public ServletRegistrationBean<CommentsServlet> commentsServlet(
            CatalogRepositoryPort bookRepo, CommentRepositoryPort commentRepo) {
        return new ServletRegistrationBean<>(new CommentsServlet(bookRepo, commentRepo), "/comments", "/comments/*");
    }

    @Bean
    public ServletRegistrationBean<jakarta.servlet.http.HttpServlet> rootServlet() {
        jakarta.servlet.http.HttpServlet rootServlet = new jakarta.servlet.http.HttpServlet() {
            @Override
            protected void doGet(jakarta.servlet.http.HttpServletRequest req, jakarta.servlet.http.HttpServletResponse resp) throws java.io.IOException {
                resp.sendRedirect("/comments");
            }
        };
        return new ServletRegistrationBean<>(rootServlet, "");
    }
}