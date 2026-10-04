package sumdu.edu.ua.web.error;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ErrorFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ErrorFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        try {
            chain.doFilter(request, response);
            // Логування 4xx помилок 
            if (resp.getStatus() >= 400 && resp.getStatus() < 500) {
                log.warn("Client error {}: {} {}", resp.getStatus(), req.getMethod(), req.getRequestURI());
            }
        } catch (Exception e) {
            // Логування 5xx помилок та перехоплення Exception
            log.error("Server error 500: {} {}", req.getMethod(), req.getRequestURI(), e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write("{\"error\": \"Internal Server Error\", \"message\": \"" + e.getMessage() + "\"}");
        }
    }
}
