package sumdu.edu.ua.web.dto;

public record BookRequest(
        String title,
        String author,
        int pubYear
) {}
