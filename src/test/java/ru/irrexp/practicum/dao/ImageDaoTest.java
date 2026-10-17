package ru.irrexp.practicum.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.irrexp.practicum.exception.ServerException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ImageDaoTest {

    @Autowired
    private ImageDao imageDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Integer POST_ID = 1;
    private byte[] imageBytes = new byte[]{0x01, 0x02, 0x03};

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("INSERT INTO posts (id, title, content) VALUES (?, ?, ?)", POST_ID, "Test post", "Content");
    }

    @Test
    void save_create_success() {
        imageDao.save(POST_ID, imageBytes);

        Optional<byte[]> stored = imageDao.findByPostId(POST_ID);
        assertTrue(stored.isPresent());
        assertArrayEquals(imageBytes, stored.get());
    }

    @Test
    void save_update_success() {
        byte[] oldBytes = new byte[]{0x01, 0x02};
        imageDao.save(POST_ID, oldBytes);

        byte[] newBytes = new byte[]{0x01, 0x02, 0x03, 0x04};
        imageDao.save(POST_ID, newBytes);

        Optional<byte[]> updated = imageDao.findByPostId(POST_ID);
        assertTrue(updated.isPresent());
        assertArrayEquals(newBytes, updated.get());
    }

    @Test
    void save_withoutPostId_throwsException() {
        ServerException ex = assertThrows(ServerException.class, () -> imageDao.save(null, imageBytes));
        assertEquals("Нельзя сохранить картинку без поста!", ex.getMessage());
    }

    @Test
    void delete_success() {
        imageDao.save(POST_ID, imageBytes);
        assertTrue(imageDao.findByPostId(POST_ID).isPresent());

        imageDao.delete(POST_ID);

        assertFalse(imageDao.findByPostId(POST_ID).isPresent());
    }

    @Test
    void findByPostId_notFound_returnsEmpty() {
        Optional<byte[]> result = imageDao.findByPostId(POST_ID);
        assertFalse(result.isPresent());
    }
}
