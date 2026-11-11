package sumdu.edu.ua.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;
import sumdu.edu.ua.core.exceptions.InvalidCommentTextException;

@ControllerAdvice(annotations = Controller.class)
public class GlobalMvcExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalMvcExceptionHandler.class);

    @ExceptionHandler({CommentTooOldException.class, InvalidCommentDeleteException.class, InvalidCommentTextException.class})
    public String handleMvcBusinessExceptions(RuntimeException ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        log.warn("MVC Exception intercepted: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/books");
    }
}
