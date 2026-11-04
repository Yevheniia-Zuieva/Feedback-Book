package sumdu.edu.ua.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sumdu.edu.ua.core.domain.BookEntity;

public interface BookRepository extends JpaRepository<BookEntity, Long> {}