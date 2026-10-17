package ru.irrexp.practicum.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.irrexp.practicum.model.Post;
import ru.irrexp.practicum.dto.PostsPageDto;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PostDaoTest {

    @Autowired
    private PostDao postDao;
    @Autowired
    private TagDao tagDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM posts WHERE 1=1");
        jdbcTemplate.update("DELETE FROM tags WHERE 1=1");
    }

    @Test
    void find_found_success() {
        Integer postId = 1;
        jdbcTemplate.update("INSERT INTO posts (id, title, content, likes_count, comments_count) VALUES (?, ?, ?, ?, ?)",
                            postId, "Заголовок", "Текст поста", 0, 0);

        Optional<Post> result = postDao.find(postId);
        assertTrue(result.isPresent());
        assertEquals("Заголовок", result.get().getTitle());
    }

    @Test
    void find_notFound_returnsEmpty() {
        Optional<Post> result = postDao.find(999);
        assertFalse(result.isPresent());
    }

    @Test
    void save_create_success() {
        Post post = new Post();
        post.setTitle("Новый пост");
        post.setText("Текст нового поста");
        Set<String> tags = new HashSet<>(Set.of("java", "spring"));
        post.setTags(tags);

        Post saved = postDao.save(post);

        assertNotNull(saved.getId());
        assertEquals(0, saved.getLikesCount());
        assertEquals(0, saved.getCommentsCount());

        List<String> storedTags = jdbcTemplate.queryForList(
                "SELECT tag FROM tags WHERE post_id = ?",
                String.class,
                saved.getId()
        );
        assertEquals(tags, new HashSet<>(storedTags));
    }

    @Test
    void save_update_success() {
        Integer existingId = 2;
        jdbcTemplate.update("INSERT INTO posts (id, title, content, likes_count, comments_count) VALUES (?, ?, ?, ?, ?)",
                            existingId, "Старый заголовок", "Старый текст", 5, 3);

        Post post = new Post();
        post.setId(existingId);
        post.setTitle("Обновлённый заголовок");
        post.setText("Обновлённый текст");
        post.setLikesCount(10);
        post.setCommentsCount(7);
        Set<String> newTags = new HashSet<>(Set.of("kotlin", "gradle"));
        post.setTags(newTags);

        Post updated = postDao.save(post);

        assertEquals(existingId, updated.getId());
        assertEquals("Обновлённый заголовок", updated.getTitle());
        assertEquals(10, updated.getLikesCount());
        assertEquals(7, updated.getCommentsCount());

        List<String> storedTags = jdbcTemplate.queryForList(
                "SELECT tag FROM tags WHERE post_id = ?",
                String.class,
                updated.getId()
        );
        assertEquals(newTags, new HashSet<>(storedTags));
    }

    @Test
    void like_success() {
        Post post = new Post();
        post.setTitle("Новый пост");
        post.setText("Текст нового поста");
        Set<String> tags = new HashSet<>(Set.of("java", "spring"));
        post.setTags(tags);

        Post saved = postDao.save(post);

        postDao.like(saved);

        Long likes = jdbcTemplate.queryForObject("SELECT likes_count FROM posts WHERE id = ?", Long.class, saved.getId());
        assertEquals(1L, likes);
        assertEquals(1, post.getLikesCount());
    }

    @Test
    void delete_success() {
        Integer postId = 4;
        jdbcTemplate.update("INSERT INTO posts (id, title, content, likes_count, comments_count) VALUES (?, ?, ?, ?, ?)",
                            postId, "Удаляемый пост", "Текст", 0, 0);
        var newTags = Set.of("tag1", "tag2");
        tagDao.addNewTags(postId, newTags);

        postDao.delete(postId);

        assertFalse(postDao.find(postId).isPresent());
        List<String> storedTags = jdbcTemplate.queryForList(
                "SELECT tag FROM tags WHERE post_id = ?",
                String.class,
                postId
        );
        assertEquals(0, storedTags.size());
    }

    @Test
    void search_withTitleSubstring_success() {
        jdbcTemplate.update("INSERT INTO posts (id, title, content, likes_count, comments_count) VALUES (?, ?, ?, ?, ?)", 1, "Java статья", "Текст 1", 0, 0);
        jdbcTemplate.update("INSERT INTO posts (id, title, content, likes_count, comments_count) VALUES (?, ?, ?, ?, ?)", 2, "Spring статья", "Текст 2", 0, 0);

        PostsPageDto result = postDao.search("Java", Set.of(), 1, 10);
        assertEquals(1, result.lastPage());
        assertEquals(1, result.posts().size());
    }

    @Test
    void search_withTags_success() {
        Integer postId = 5;
        jdbcTemplate.update("INSERT INTO posts (id, title, content, likes_count, comments_count) VALUES (?, ?, ?, ?, ?)",
                            postId, "Пост с тегами", "Текст", 0, 0);
        tagDao.addNewTags(postId, Set.of("java"));

        PostsPageDto result = postDao.search(null, Set.of("java"), 1, 10);
        assertEquals(1, result.lastPage());
        assertEquals(1, result.posts().size());
    }
}
