package sumdu.edu.ua.core.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sumdu.edu.ua.core.domain.CommentEntity;
import sumdu.edu.ua.core.port.CommentRepositoryPort;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepositoryPort commentRepositoryPort;

    public CommentService(CommentRepositoryPort commentRepositoryPort) {
        this.commentRepositoryPort = commentRepositoryPort;
    }

    public List<CommentEntity> getCommentsByUser(Long userId) {
        return commentRepositoryPort.findByUserId(userId);
    }
}