package ru.irrexp.practicum.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.irrexp.practicum.dao.CommentDao;
import ru.irrexp.practicum.exception.ServerException;
import ru.irrexp.practicum.model.Comment;
import ru.irrexp.practicum.util.FileLoaderUtil;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class CommentDaoImpl implements CommentDao {

    private final NamedParameterJdbcTemplate template;

    @Override
    public Stream<Comment> findAllCommentsByPostId(Integer postId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId);
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/comment/find-post-comments.sql");

        return template.queryForStream(sql, namedParameters, BeanPropertyRowMapper.newInstance(Comment.class));
    }

    @Override
    public Optional<Comment> find(Integer postId, Integer commentId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("commentId", commentId);
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/comment/find-comment.sql");

        return Optional.ofNullable(template.queryForObject(sql, namedParameters, BeanPropertyRowMapper.newInstance(Comment.class)));
    }

    @Override
    public void save(Comment comment) {

        if (comment.getPostId() == null) throw new ServerException("Нельзя сохранить комментарий без поста!");

        if (comment.getId() == null) {
            create(comment);
        } else {
            update(comment);
        }

    }

    private void create(Comment comment) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", comment.getPostId())
                .addValue("content", comment.getContent());

        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/comment/create-comment.sql");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, namedParameters, keyHolder);

        Integer commentId = Optional.ofNullable(keyHolder.getKeys())
                .map(k -> k.get("id"))
                .map(id -> (Integer) id)
                .orElseThrow(() -> new IllegalStateException("Не удалось получить сгенерированный ID созданного поста"));

        comment.setId(commentId);

        changeCommentsCount(comment.getPostId(), 1);

    }

    private void changeCommentsCount(Integer postId, Integer modifier) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("modifier", modifier);
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/change-comments-count.sql");
        template.update(sql, namedParameters);
    }

    private void update(Comment comment) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", comment.getPostId())
                .addValue("content", comment.getContent())
                .addValue("commentId", comment.getId());

        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/comment/update-comment.sql");
        template.update(sql, namedParameters);
    }

    @Override
    public void delete(Integer postId, Integer id) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("commentId", id);
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/comment/delete-comment.sql");

        template.update(sql, namedParameters);
        changeCommentsCount(postId, -1);
    }

}
