package sumdu.edu.ua.persistence.adapter;

import org.springframework.stereotype.Repository;
import sumdu.edu.ua.core.domain.BookEntity;
import sumdu.edu.ua.core.domain.Page;
import sumdu.edu.ua.core.domain.PageRequest;
import sumdu.edu.ua.core.port.CatalogRepositoryPort;
import sumdu.edu.ua.persistence.repository.BookRepository;

import java.util.Optional;

@Repository
public class JpaCatalogRepositoryAdapter implements CatalogRepositoryPort {

    private final BookRepository repo;

    public JpaCatalogRepositoryAdapter(BookRepository repo) {
        this.repo = repo;
    }

    @Override
    public Page<BookEntity> search(String query, PageRequest request) {
        var pageable = org.springframework.data.domain.PageRequest.of(
                request.getPage(), request.getSize()
        );
        var page = repo.findAll(pageable);
        return new Page<>(page.getContent(), request, page.getTotalElements());
    }

    @Override
    public Optional<BookEntity> findById(long id) {
        return repo.findById(id);
    }

    @Override
    public BookEntity add(String title, String author, int pubYear) {
        BookEntity book = new BookEntity(null, title, author, pubYear);
        return repo.save(book);
    }
}
