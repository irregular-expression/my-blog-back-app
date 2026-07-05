package ru.irrexp.practicum.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.irrexp.practicum.dao.PostDao;
import ru.irrexp.practicum.dto.PostsPageDto;
import ru.irrexp.practicum.mapper.PostMapper;
import ru.irrexp.practicum.service.impl.PostServiceImpl;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostDao postDao;

    @Mock
    private PostMapper postMapper;


    @Test
    void shouldDoSearchLogic() {

        PostsPageDto dto = PostsPageDto.builder()
                .build();

        doReturn(dto).when(postDao).search(eq("Привет мир!"), eq(Set.of("#мир")), eq(1), eq(1));

        var result = postService.search("Привет мир! #мир", 1, 1);

        verify(postDao, times(1)).search(eq("Привет мир!"), eq(Set.of("#мир")), eq(1), eq(1));
        assertEquals(dto, result);

    }

}
