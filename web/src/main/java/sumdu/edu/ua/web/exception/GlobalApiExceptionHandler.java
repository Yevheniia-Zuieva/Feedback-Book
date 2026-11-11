package sumdu.edu.ua.web.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import sumdu.edu.ua.core.exceptions.BookNotFoundException;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);

    @ExceptionHandler(InvalidCommentDeleteException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDelete(InvalidCommentDeleteException ex) {
        log.info("Invalid comment delete request: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommentTooOldException.class)
    public ResponseEntity<Map<String, String>> handleTooOld(CommentTooOldException ex) {
        log.info("Comment too old to delete: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookNotFound(BookNotFoundException ex) {
        log.info("Book not found: {}", ex.getMessage());
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}