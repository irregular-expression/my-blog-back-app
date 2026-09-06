package ru.irrexp.practicum.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.irrexp.practicum.dao.CommentDao;
import ru.irrexp.practicum.dto.CommentDto;
import ru.irrexp.practicum.dto.CreateCommentRq;
import ru.irrexp.practicum.mapper.CommentMapper;
import ru.irrexp.practicum.model.Comment;
import ru.irrexp.practicum.service.CommentService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentDao dao;
    private final CommentMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllByPostId(Integer postId) {
        return dao.findAllCommentsByPostId(postId)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CommentDto> find(Integer postId, Integer commentId) {
        return dao.find(postId, commentId)
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public CommentDto create(Integer postId, CreateCommentRq createCommentRq) {
        Comment comment = mapper.toEntity(createCommentRq);
        dao.save(comment);
        return mapper.toDto(comment);
    }

    @Override
    @Transactional
    public Optional<CommentDto> update(Integer postId, Integer commentId, CommentDto dto) {
        return dao.find(postId, commentId)
                .map(c -> confirmedUpdate(c, dto))
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public boolean delete(Integer postId, Integer commentId) {
        return dao.find(postId, commentId)
                .map(c -> confirmedDelete(postId, c))
                .orElse(Boolean.FALSE);
    }

    private boolean confirmedDelete(@NonNull Integer postId, @NonNull Comment comment) {
        dao.delete(postId, comment.getId());
        return true;
    }

    private Comment confirmedUpdate(@NonNull Comment comment, @NonNull CommentDto dto) {
        Comment updatedComment = mapper.toEntity(comment, dto);
        dao.save(updatedComment);
        return updatedComment;
    }
}
