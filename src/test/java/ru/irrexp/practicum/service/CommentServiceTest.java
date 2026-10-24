package ru.irrexp.practicum.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import ru.irrexp.practicum.dao.CommentDao;
import ru.irrexp.practicum.dto.CommentDto;
import ru.irrexp.practicum.dto.CreateCommentRq;
import ru.irrexp.practicum.mapper.CommentMapper;
import ru.irrexp.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @MockitoBean
    private CommentDao commentDao;

    @MockitoSpyBean
    private CommentMapper commentMapper;

    @Test
    void getAllByPostId_returnsListFromDaoAndMapped() {
        Integer postId = 1;
        Comment comment = new Comment();
        comment.setId(101);
        comment.setContent("test");
        comment.setPostId(postId);

        List<Comment> comments = List.of(comment);
        when(commentDao.findAllCommentsByPostId(postId)).thenReturn(comments.stream());

        List<CommentDto> result = commentService.getAllByPostId(postId);

        assertEquals(1, result.size());
        assertEquals("test", result.get(0).text());
        verify(commentDao).findAllCommentsByPostId(postId);
    }

    @Test
    void find_returnsOptionalDto() {
        Integer postId = 1, commentId = 101;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setContent("found");
        comment.setPostId(postId);

        Optional<Comment> optComment = Optional.of(comment);
        when(commentDao.find(postId, commentId)).thenReturn(optComment);

        Optional<CommentDto> result = commentService.find(postId, commentId);

        assertTrue(result.isPresent());
        assertEquals("found", result.get().text());
        verify(commentDao).find(postId, commentId);
    }

    @Test
    void create_usesPostIdFromBodyIfNotNull() {
        Integer postIdParam = 1;
        CreateCommentRq request = CreateCommentRq.builder()
                .postId(2)
                .text("body-postId")
                .build();

        Comment fromRequest = new Comment();
        fromRequest.setContent("body-postId");
        fromRequest.setPostId(2);

        Comment saved = new Comment();
        saved.setId(201);
        saved.setContent("body-postId");
        saved.setPostId(2);
        doNothing().when(commentDao).save(any(Comment.class));

        CommentDto result = commentService.create(postIdParam, request);

        assertEquals(2, result.postId());
        verify(commentDao).save(argThat(c -> c.getPostId() == 2));
        verify(commentMapper).toEntity(request);
    }

    @Test
    void create_fallsBackToPostIdParamIfBodyPostIdIsNull() {
        Integer postIdParam = 3;
        CreateCommentRq request = CreateCommentRq.builder()
                .postId(null)
                .text("param-postId")
                .build();

        Comment fromRequest = new Comment();
        fromRequest.setContent("param-postId");
        fromRequest.setPostId(null);

        doNothing().when(commentDao).save(any(Comment.class));

        CommentDto result = commentService.create(postIdParam, request);

        assertEquals(3, result.postId());
        verify(commentDao).save(argThat(c -> c.getPostId() == 3));
        verify(commentMapper).toEntity(request);
    }

    @Test
    void update_mapsAndSaves() {
        Integer postId = 4, commentId = 401;
        Comment existing = new Comment();
        existing.setId(commentId);
        existing.setContent("old");
        existing.setPostId(postId);

        CommentDto updateDto = CommentDto.builder()
                .text("new")
                .build();

        when(commentDao.find(postId, commentId)).thenReturn(Optional.of(existing));

        Comment updatedEntity = new Comment();
        updatedEntity.setId(commentId);
        updatedEntity.setContent("new");
        updatedEntity.setPostId(postId);

        Optional<CommentDto> result = commentService.update(postId, commentId, updateDto);

        assertTrue(result.isPresent());
        assertEquals("new", result.get().text());
        verify(commentDao, times(1)).find(postId, commentId);
        verify(commentDao, times(1)).save(updatedEntity);
        verify(commentMapper).toEntity(existing, updateDto);
        verify(commentMapper).toDto(updatedEntity);
    }

    @Test
    void delete_returnsTrueIfFoundAndDeleted() {
        Integer postId = 5, commentId = 501;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setPostId(postId);

        when(commentDao.find(postId, commentId)).thenReturn(Optional.of(comment));

        doNothing().when(commentDao).delete(postId, commentId);

        boolean result = commentService.delete(postId, commentId);

        assertTrue(result);
        verify(commentDao).find(postId, commentId);
        verify(commentDao).delete(postId, commentId);
    }
}

