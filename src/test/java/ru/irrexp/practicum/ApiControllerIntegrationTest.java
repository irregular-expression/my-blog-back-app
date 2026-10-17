package ru.irrexp.practicum;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.irrexp.practicum.dto.CommentDto;
import ru.irrexp.practicum.dto.CreateCommentRq;
import ru.irrexp.practicum.dto.CreatePostRq;
import ru.irrexp.practicum.dto.UpdatePostRq;
import ru.irrexp.practicum.util.FileLoaderUtil;

import java.sql.SQLException;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiControllerIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private static final Integer POST_ID = 1;
    private static final Integer COMMENT_ID = 1;

    @BeforeEach
    void setup() throws SQLException {

        jdbcTemplate.update("INSERT INTO posts (id, title, content, comments_count) VALUES (?, ?, ?, ?)",
                            POST_ID, "Привет мир!", "Content", 1);
        jdbcTemplate.update("INSERT INTO tags (post_id, tag) VALUES (?, ?)",
                POST_ID, "#мир");
        jdbcTemplate.update(
                "INSERT INTO comments (id, post_id, content) VALUES (?, ?, ?)",
                COMMENT_ID, POST_ID, "Тестовый комментарий"
        );

    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute("TRUNCATE TABLE posts RESTART IDENTITY CASCADE");
    }

    @Test
    void shouldGetPosts() throws Exception {
        mockMvc.perform(get("/posts")
                                .param("search", "мир")
                                .param("pageNumber", "1")
                                .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".posts[0].title").value("Привет мир!"));

        mockMvc.perform(get("/posts")
                                .param("search", "Привет #мир")
                                .param("pageNumber", "1")
                                .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".posts[0].title").value("Привет мир!"));

    }

    @Test
    void shouldCreatePost() throws Exception {

        var request = CreatePostRq.builder()
                .title("Про кроликов")
                .text("Кролики - это не только ценный мех")
                .tags(List.of("#кролики")).build();

        mockMvc.perform(post("/posts")
                                               .contentType(MediaType.APPLICATION_JSON)
                                               .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".title").value("Про кроликов"));

    }

    @Test
    void shouldGetPost() throws Exception {
        mockMvc.perform(get("/post/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".title").value("Привет мир!"));
    }

    @Test
    void shouldEditPost() throws Exception {
        var request = UpdatePostRq.builder()
                .id(1)
                .title("Суслик")
                .text("Пост про сусликов, а не про мир")
                .tags(List.of("#суслик"))
                .build();

        mockMvc.perform(put("/posts/{id}", request.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/post/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".title").value("Суслик"));
    }

    @Test
    void shouldSearchPage() throws Exception {
        for (int i = 2; i <= 50; i++) {
            jdbcTemplate.execute("""
                    INSERT INTO posts (id, title, content)
                    VALUES (%d, 'Привет мир! - %d', 'Тестовый пост')
                """.formatted(i, i));

            jdbcTemplate.execute("""
                    INSERT INTO tags (post_id, tag)
                    VALUES (%d, '#мир')
                """.formatted(i));
        }


        mockMvc.perform(get("/posts")
                                .param("search", "мир")
                                .param("pageNumber", "1")
                                .param("pageSize", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".hasPrev").value(Boolean.FALSE))
                .andExpect(jsonPath(".hasNext").value(Boolean.TRUE))
                .andExpect(jsonPath(".lastPage").value(50));

    }

    @Test
    void shouldLikePost() throws Exception {
        mockMvc.perform(post("/posts/{id}/likes", 1))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReadComment() throws Exception {
        mockMvc.perform(get("/posts/{id}/comments", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].text").value("Тестовый комментарий"));

    }

    @Test
    void shouldGetComment() throws Exception {
        mockMvc.perform(get("/posts/{postId}/comments/{commentId}", 1, 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".text").value("Тестовый комментарий"));

    }

    @Test
    void shouldCreateComment() throws Exception {
        var request = CreateCommentRq.builder()
                .postId(1)
                .text("Кролики - это не только ценный мех")
                .build();

        mockMvc.perform(post("/posts/{id}/comments", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".text").value("Кролики - это не только ценный мех"));
    }

    @Test
    void shouldEditComment() throws Exception {
        var request = CommentDto.builder()
                .postId(1)
                .id(1)
                .text("Кролики - это не только ценный мех")
                .build();

        mockMvc.perform(put("/posts/{postId}/comments/{commentId}", 1, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath(".text").value("Кролики - это не только ценный мех"));
    }


    @Test
    void shouldDeletePost() throws Exception {
        mockMvc.perform(delete("/posts/{id}", 1))
                .andExpect(status().isOk());

    }

    @Test
    void shouldDeleteComment() throws Exception {
        mockMvc.perform(delete("/posts/{postId}/comments/{commentId}", 1, 1))
                .andExpect(status().isOk());

    }

    @Test
    void shouldUploadAndDownloadImage() throws Exception {
        byte[] jpgStub = FileLoaderUtil.loadByteArrayFromClasspath("test-image.jpg");
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", jpgStub);

        mockMvc.perform(multipart(HttpMethod.PUT, "/posts/{id}/image", 1)
                                .file(file))
                .andExpect(status().isOk());

        mockMvc.perform(get("/posts/{postId}/image", 1L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(jpgStub));

    }

}
