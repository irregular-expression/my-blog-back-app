package ru.irrexp.practicum.dao;

import ru.irrexp.practicum.model.Comment;

import java.util.Optional;
import java.util.stream.Stream;

public interface CommentDao {

    Stream<Comment> findAllCommentsByPostId(Integer postId);

    Optional<Comment> find(Integer postId, Integer commentId);

    void save(Comment comment);

    void delete(Integer postId, Integer id);

}
