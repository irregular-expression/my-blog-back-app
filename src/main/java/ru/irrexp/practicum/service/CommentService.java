package ru.irrexp.practicum.service;

import ru.irrexp.practicum.dto.CommentDto;
import ru.irrexp.practicum.dto.CreateCommentRq;

import java.util.List;
import java.util.Optional;

public interface CommentService {

    List<CommentDto> getAllByPostId(Integer postId);

    Optional<CommentDto> find(Integer postId, Integer commentId);

    CommentDto create(Integer postId, CreateCommentRq createCommentRq);

    Optional<CommentDto> update(Integer postId, Integer commentId, CommentDto comment);

    boolean delete(Integer postId, Integer commentId);

}
