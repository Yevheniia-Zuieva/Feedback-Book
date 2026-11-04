package sumdu.edu.ua.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import sumdu.edu.ua.core.domain.BookEntity;

import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final EmailTemplateProcessor templateProcessor;

    @Value("${spring.mail.username}")
    private String adminEmail;

    public MailService(JavaMailSender mailSender, EmailTemplateProcessor templateProcessor) {
        this.mailSender = mailSender;
        this.templateProcessor = templateProcessor;
    }

    public void sendNewBookEmail(BookEntity book) {
        Map<String, Object> model = new HashMap<>();
        model.put("title", book.getTitle());
        model.put("author", book.getAuthor());
        model.put("year", book.getYear());
        model.put("added", Date.from(Instant.now()));

        String html = templateProcessor.process("new_book.ftl", model);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
            helper.setTo(adminEmail);
            helper.setSubject("Нова книга в каталозі");
            helper.setText(html, true);
            helper.setFrom(adminEmail);

            mailSender.send(message);
            log.info("Email sent successfully to {}", adminEmail);

        } catch (Exception e) {
            log.error("Email sending failed", e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
}