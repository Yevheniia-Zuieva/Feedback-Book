package sumdu.edu.ua.web.dto;

public record CommentRequest(
        long bookId,
        String author,
        String text
) {}