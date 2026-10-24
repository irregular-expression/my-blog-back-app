package ru.irrexp.practicum.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.irrexp.practicum.dao.PostDao;
import ru.irrexp.practicum.model.Post;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class LikeServiceTest {

    @Autowired
    private LikeService likeService;

    @MockitoBean
    private PostDao postDao;

    @Test
    void like_successWhenPostExists() {
        Integer postId = 1;
        Post post = new Post();
        post.setId(postId);
        post.setTitle("test post");

        when(postDao.find(postId)).thenReturn(Optional.of(post));

        boolean result = likeService.like(postId);

        assertTrue(result);
        verify(postDao).find(postId);
        verify(postDao).like(post);
    }

    @Test
    void like_returnsFalseWhenPostNotFound() {
        Integer postId = 999;
        when(postDao.find(postId)).thenReturn(Optional.empty());

        boolean result = likeService.like(postId);

        assertFalse(result);
        verify(postDao).find(postId);
        verifyNoMoreInteractions(postDao);
    }
}

