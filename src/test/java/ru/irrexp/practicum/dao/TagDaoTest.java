package ru.irrexp.practicum.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TagDaoTest {

    @Autowired
    private TagDao tagDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Integer POST_ID = 1;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO posts (id, title, content) VALUES (?, ?, ?)",
                              POST_ID, "Test post", "Content");
    }

    @Test
    void addNewTags_success() {
        Set<String> newTags = Set.of("java", "spring", "gradle");

        tagDao.addNewTags(POST_ID, newTags);

        var storedTags = jdbcTemplate.queryForList(
                "SELECT tag FROM tags WHERE post_id = ?",
                String.class,
                POST_ID
        );

        assertEquals(newTags.size(), storedTags.size());
        assertTrue(storedTags.containsAll(newTags));
    }

    @Test
    void deleteTags_withTags_success() {
        jdbcTemplate.update(
                "INSERT INTO tags (post_id, tag) VALUES (?, ?)",
                POST_ID, "java");
        jdbcTemplate.update(
                "INSERT INTO tags (post_id, tag) VALUES (?, ?)",
                POST_ID, "spring");
        jdbcTemplate.update(
                "INSERT INTO tags (post_id, tag) VALUES (?, ?)",
                POST_ID, "gradle");

        Set<String> expiredTags = Set.of("spring", "gradle");

        tagDao.deleteTags(POST_ID, expiredTags);

        var remainingTags = jdbcTemplate.queryForList(
                "SELECT tag FROM tags WHERE post_id = ?",
                String.class,
                POST_ID
        );

        assertEquals(1, remainingTags.size());
        assertEquals("java", remainingTags.get(0));
    }

    @Test
    void deleteTags_deleteAll_success() {
        jdbcTemplate.update(
                "INSERT INTO tags (post_id, tag) VALUES (?, ?)",
                POST_ID, "java");

        Set<String> expiredTags = Set.of();

        tagDao.deleteTags(POST_ID, expiredTags);

        var remainingTags = jdbcTemplate.queryForList(
                "SELECT tag FROM tags WHERE post_id = ?",
                String.class,
                POST_ID
        );

        assertEquals(0, remainingTags.size());
    }

    @Test
    void updateTags_addAndRemove_success() {
        jdbcTemplate.update(
                "INSERT INTO tags (post_id, tag) VALUES (?, ?)",
                POST_ID, "java");
        jdbcTemplate.update(
                "INSERT INTO tags (post_id, tag) VALUES (?, ?)",
                POST_ID, "spring");

        Set<String> updatedTags = Set.of("java", "gradle", "kotlin");

        tagDao.updateTags(POST_ID, updatedTags);

        var finalTags = jdbcTemplate.queryForList(
                "SELECT tag FROM tags WHERE post_id = ? ORDER BY tag",
                String.class,
                POST_ID
        );

        assertEquals(3, finalTags.size());
        assertEquals(Set.of("java", "gradle", "kotlin"), new HashSet<>(finalTags));
    }

    @Test
    void updateTags_noChanges_success() {
        jdbcTemplate.update(
                "INSERT INTO tags (post_id, tag) VALUES (?, ?)",
                POST_ID, "java");
        jdbcTemplate.update(
                "INSERT INTO tags (post_id, tag) VALUES (?, ?)",
                POST_ID, "spring");

        Set<String> sameTags = Set.of("java", "spring");

        tagDao.updateTags(POST_ID, sameTags);

        var finalTags = jdbcTemplate.queryForList(
                "SELECT tag FROM tags WHERE post_id = ? ORDER BY tag",
                String.class,
                POST_ID
        );

        assertEquals(2, finalTags.size());
        assertEquals(Set.of("java", "spring"), new HashSet<>(finalTags));
    }
}
