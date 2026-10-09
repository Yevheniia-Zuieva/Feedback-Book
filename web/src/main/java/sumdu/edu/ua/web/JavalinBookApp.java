package sumdu.edu.ua.web;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sumdu.edu.ua.web.controller.BooksController;
import sumdu.edu.ua.web.controller.CommentsController;
import sumdu.edu.ua.config.Beans;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.json.JavalinJackson;

import java.util.Map;

public class JavalinBookApp {

    private static final Logger log = LoggerFactory.getLogger(JavalinBookApp.class);

    public static void main(String[] args) {

        // Ініціалізація бази даних та бінів 
        Beans.init();

        var app = Javalin.create(config -> {
            // Налаштовуємо ObjectMapper з підтримкою JavaTimeModule
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());

            // Передаємо оновлений ObjectMapper через JavalinJackson
            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> mapper.registerModule(new JavaTimeModule())));

            // Підключаємо статичний HTML/JS клієнт
            config.staticFiles.add("/public", Location.CLASSPATH);
        });

        // 1. Middleware для логування (before & after)
        app.before(ctx -> log.info("-> Request: {} {}", ctx.method(), ctx.path()));
        app.after(ctx -> {
            if (ctx.status().getCode() >= 400 && ctx.status().getCode() < 500) {
                log.warn("<- Client Response: {} {}", ctx.status().getCode(), ctx.path());
            } else {
                log.info("<- Response: {} {}", ctx.status().getCode(), ctx.path());
            }
        });

        // 2. Централізована обробка винятків (Exception Handling)
        app.exception(Exception.class, (e, ctx) -> {
            log.error("Unhandled server error on {} {}", ctx.method(), ctx.path(), e);
            ctx.status(500).json(Map.of("error", "Internal server error", "message", e.getMessage()));
        });

        // 3. Реєстрація REST-маршрутів
        new BooksController(Beans.getBookRepo()).registerRoutes(app);
        new CommentsController(Beans.getCommentRepo()).registerRoutes(app);

        // 4. Перенаправлення кореневого шляху "/" на статичний HTML
        app.get("/", ctx -> ctx.redirect("/index.html"));

        // 5. Запуск сервера Jetty
        app.start(8080);
        log.info("App started at: http://localhost:8080/");
    }
}