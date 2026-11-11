package sumdu.edu.ua.core.service;

import org.junit.jupiter.api.Test;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.exceptions.InvalidCommentDeleteException;
import sumdu.edu.ua.core.exceptions.InvalidCommentTextException;
import sumdu.edu.ua.core.port.CommentRepositoryPort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class CommentServiceTest {

    @Test
    void delete_withNegativeIds_throwsInvalidCommentDeleteException() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        assertThrows(InvalidCommentDeleteException.class,
                () -> service.delete(-1L, 0L, Instant.now()));

        verifyNoInteractions(repo);
    }

    @Test
    void delete_withoutCreatedAt_throwsInvalidCommentDeleteException() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        assertThrows(InvalidCommentDeleteException.class,
                () -> service.delete(1L, 1L, null));

        verifyNoInteractions(repo);
    }

    @Test
    void delete_tooOldComment_throwsCommentTooOldException() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        Instant twoDaysAgo = Instant.now().minus(48, ChronoUnit.HOURS);

        assertThrows(CommentTooOldException.class,
                () -> service.delete(1L, 1L, twoDaysAgo));

        verifyNoInteractions(repo);
    }

    @Test
    void delete_validData_callsRepositoryDelete() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);

        service.delete(10L, 20L, oneHourAgo);

        verify(repo).delete(10L, 20L);
    }

    @Test
    void addComment_emptyText_throwsInvalidCommentTextException() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        assertThrows(InvalidCommentTextException.class, () -> service.addComment(1L, 1L, "   "));
        verifyNoInteractions(repo);
    }

    @Test
    void addComment_spamWord_throwsInvalidCommentTextException() {
        CommentRepositoryPort repo = mock(CommentRepositoryPort.class);
        CommentService service = new CommentService(repo);

        assertThrows(InvalidCommentTextException.class, () -> service.addComment(1L, 1L, "This is spam message"));
        verifyNoInteractions(repo);
    }
}