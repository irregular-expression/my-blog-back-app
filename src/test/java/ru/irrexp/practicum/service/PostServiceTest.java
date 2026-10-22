package ru.irrexp.practicum.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.irrexp.practicum.dao.PostDao;
import ru.irrexp.practicum.dto.PostDto;
import ru.irrexp.practicum.dto.PostsPageDto;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class PostServiceTest {

    @Autowired
    private PostService postService;

    @MockitoBean
    public PostDao postDao;

    private static final Integer PAGE_NUMBER = 1;
    private static final Integer PAGE_SIZE = 10;


    @Test
    void search_mixedQuery_titleAndTagsExtractedCorrectly() {
        String query = "java статья #spring #boot";

        PostsPageDto expectedResult = PostsPageDto.builder()
                .posts(List.of(PostDto.builder().build()))
                .build();

        when(postDao.search(eq("java статья"), anySet(), anyInt(), anyInt()))
                .thenReturn(expectedResult);

        PostsPageDto result = postService.search(query, PAGE_NUMBER, PAGE_SIZE);

        assertEquals(expectedResult, result);

        verify(postDao, times(1)).search(
                eq("java статья"),
                argThat(tags -> tags.contains("#spring") && tags.contains("#boot")),
                eq(PAGE_NUMBER),
                eq(PAGE_SIZE)
        );
    }

    @Test
    void search_onlyHashtags_titleEmpty_tagsCollected() {
        String query = "#java #spring";

        PostsPageDto expectedResult = PostsPageDto.builder()
                .posts(List.of(PostDto.builder().build()))
                .hasPrev(false)
                .hasNext(false)
                .build();

        when(postDao.search(eq(""), anySet(), anyInt(), anyInt()))
                .thenReturn(expectedResult);

        PostsPageDto result = postService.search(query, PAGE_NUMBER, PAGE_SIZE);

        assertEquals(expectedResult, result);

        verify(postDao, times(1)).search(
                eq(""),
                argThat(tags -> tags.contains("#java") && tags.contains("#spring")),
                eq(PAGE_NUMBER),
                eq(PAGE_SIZE)
        );
    }

    @Test
    void search_noHashtags_allWordsGoToTitle() {
        String query = "новая статья про Java";

        PostsPageDto expectedResult = PostsPageDto.builder()
                .posts(List.of(PostDto.builder()
                                       .title(query)
                                       .text(query)
                                       .build()))
                .hasNext(false)
                .hasPrev(false)
                .build();
        when(postDao.search(eq("новая статья про Java"), eq(new HashSet<>()), anyInt(), anyInt()))
                .thenReturn(expectedResult);

        PostsPageDto result = postService.search(query, PAGE_NUMBER, PAGE_SIZE);

        assertEquals(expectedResult, result);

        verify(postDao, times(1)).search(
                eq("новая статья про Java"),
                eq(new HashSet<>()),
                eq(PAGE_NUMBER),
                eq(PAGE_SIZE)
        );
    }

    @Test
    void search_emptyQuery_titleEmpty_noTags() {
        String query = "";

        PostsPageDto expectedResult = PostsPageDto.builder()
                .hasNext(false)
                .hasPrev(false)
                .lastPage(0)
                .build();
        when(postDao.search(eq(""), eq(new HashSet<>()), anyInt(), anyInt()))
                .thenReturn(expectedResult);

        PostsPageDto result = postService.search(query, PAGE_NUMBER, PAGE_SIZE);

        assertEquals(expectedResult, result);

        verify(postDao, times(1)).search(
                eq(""),
                eq(new HashSet<>()),
                eq(PAGE_NUMBER),
                eq(PAGE_SIZE)
        );
    }
}
