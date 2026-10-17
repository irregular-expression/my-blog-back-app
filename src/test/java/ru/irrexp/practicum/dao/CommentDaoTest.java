package ru.irrexp.practicum.dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.irrexp.practicum.exception.ServerException;
import ru.irrexp.practicum.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CommentDaoTest {

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Integer POST_ID = 1;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO posts (id, title, content) VALUES (?, ?, ?)",
                            POST_ID, "Test post", "Content");
    }

    @Test
    void findAllCommentsByPostId_success() {
        insertComment(POST_ID, 1, "Первый комментарий");
        insertComment(POST_ID, 2, "Второй комментарий");

        Stream<Comment> comments = commentDao.findAllCommentsByPostId(POST_ID);
        List<Comment> list = comments.toList();

        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(c -> c.getContent().equals("Первый комментарий")));
        assertTrue(list.stream().anyMatch(c -> c.getContent().equals("Второй комментарий")));
    }

    @Test
    void find_existingComment_success() {
        Integer commentId = 101;
        insertComment(POST_ID, commentId, "Комментарий для поиска");

        Optional<Comment> result = commentDao.find(POST_ID, commentId);
        assertTrue(result.isPresent());
        assertEquals("Комментарий для поиска", result.get().getContent());
    }

    @Test
    void find_notFound_returnsEmpty() {
        Optional<Comment> result = commentDao.find(POST_ID, 999);
        assertFalse(result.isPresent());
    }

    @Test
    void save_create_success() {
        Comment comment = new Comment();
        comment.setPostId(POST_ID);
        comment.setContent("Новый комментарий");

        commentDao.save(comment);

        assertNotNull(comment.getId());

        Long count = jdbcTemplate.queryForObject("SELECT comments_count FROM posts WHERE id = ?", Long.class, POST_ID);
        assertEquals(1L, count);
    }

    @Test
    void save_update_success() {
        Integer existingId = 201;
        insertComment(POST_ID, existingId, "Старый текст");

        Comment comment = new Comment();
        comment.setId(existingId);
        comment.setPostId(POST_ID);
        comment.setContent("Обновлённый текст");

        commentDao.save(comment);

        Optional<Comment> updated = commentDao.find(POST_ID, existingId);
        assertTrue(updated.isPresent());
        assertEquals("Обновлённый текст", updated.get().getContent());
        // Счётчик комментариев не должен измениться при обновлении
        Long count = jdbcTemplate.queryForObject("SELECT comments_count FROM posts WHERE id = ?", Long.class, POST_ID);
        assertEquals(1L, count);
    }

    @Test
    void save_withoutPostId_throwsException() {
        Comment comment = new Comment();
        comment.setContent("Текст");

        ServerException ex = assertThrows(ServerException.class, () -> commentDao.save(comment));
        assertEquals("Нельзя сохранить комментарий без поста!", ex.getMessage());
    }

    @Test
    void delete_success() {
        Integer commentId = 301;
        insertComment(POST_ID, commentId, "Для удаления");

        Long beforeCount = jdbcTemplate.queryForObject("SELECT comments_count FROM posts WHERE id = ?", Long.class, POST_ID);
        assertEquals(1, beforeCount);
        commentDao.delete(POST_ID, commentId);

        assertFalse(commentDao.find(POST_ID, commentId).isPresent());

        Long afterCount = jdbcTemplate.queryForObject("SELECT comments_count FROM posts WHERE id = ?", Long.class, POST_ID);
        assertEquals(0L, afterCount);
        assertEquals(beforeCount - 1, afterCount);
    }

    private void insertComment(Integer postId, Integer id, String content) {
        jdbcTemplate.update(
                "INSERT INTO comments (id, post_id, content) VALUES (?, ?, ?)",
                id, postId, content
        );
        jdbcTemplate.update("UPDATE posts SET comments_count = (SELECT COUNT(*) FROM comments WHERE post_id = ?) WHERE id = ?", postId, postId);
    }
}
