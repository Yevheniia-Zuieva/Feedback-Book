package sumdu.edu.ua.core.port;

import sumdu.edu.ua.core.domain.BookEntity;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import java.util.Optional;

public interface CatalogRepositoryPort {
    Page<BookEntity> search(String query, PageRequest request);
    Optional<BookEntity> findById(long id);
    BookEntity add(String title, String author, int pubYear);
}