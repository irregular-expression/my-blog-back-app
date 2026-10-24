package ru.irrexp.practicum.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;
import ru.irrexp.practicum.dao.ImageDao;
import ru.irrexp.practicum.exception.ServerException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ImageServiceTest {

    @Autowired
    private ImageService imageService;

    @MockitoBean
    private ImageDao imageDao;

    @Test
    void load_success() throws IOException {
        Integer postId = 1;
        byte[] bytes = "image-bytes".getBytes();

        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn(bytes);
        when(file.getName()).thenReturn("test.png");

        doNothing().when(imageDao).save(eq(postId), eq(bytes));

        boolean result = imageService.load(postId, file);

        assertTrue(result);
        verify(imageDao).save(eq(postId), eq(bytes));
    }

    @Test
    void load_throwsServerExceptionOnIOException() throws IOException {
        Integer postId = 2;

        MultipartFile file = mock(MultipartFile.class);
        IOException ioe = new IOException("read error");
        when(file.getBytes()).thenThrow(ioe);
        when(file.getName()).thenReturn("bad.png");

        ServerException thrown = assertThrows(ServerException.class, () -> {
            imageService.load(postId, file);
        });

        assertTrue(thrown.getMessage().contains("Не удалось прочитать файл bad.png"));
        verify(file).getBytes();
        verifyNoInteractions(imageDao);
    }
}
