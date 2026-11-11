package sumdu.edu.ua.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import sumdu.edu.ua.core.exceptions.CommentTooOldException;
import sumdu.edu.ua.core.service.CommentService;
import sumdu.edu.ua.web.AppInit;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = AppInit.class)
@AutoConfigureMockMvc(addFilters = false)
class CommentDeleteExceptionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @Test
    void delete_tooOldComment_returnsBadRequestWithJsonError() throws Exception {
        doThrow(new CommentTooOldException("Коментар створено більше ніж 24 години тому і не може бути видалений"))
                .when(commentService)
                .delete(eq(1L), eq(2L), any(Instant.class));

        mockMvc.perform(post("/comments/delete")
                        .param("bookId", "1")
                        .param("commentId", "2")
                        .param("createdAt", "2024-01-01T00:00:00Z"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Коментар створено більше ніж 24 години тому і не може бути видалений"));
    }
}