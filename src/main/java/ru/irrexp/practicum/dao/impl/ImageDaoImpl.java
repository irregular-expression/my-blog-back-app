package ru.irrexp.practicum.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.irrexp.practicum.dao.ImageDao;
import ru.irrexp.practicum.exception.ServerException;
import ru.irrexp.practicum.util.FileLoaderUtil;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ImageDaoImpl implements ImageDao {

    private final NamedParameterJdbcTemplate template;

    @Override
    public void save(Integer postId, byte[] bytes) {
        if (postId == null) throw new ServerException("Нельзя сохранить картинку без поста!");

        if (!imageExists(postId)) {
            create(postId, bytes);
        } else {
            update(postId, bytes);
        }
    }

    private boolean imageExists(Integer postId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId);
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/image/image-exists.sql");

        return Boolean.TRUE.equals(template.queryForObject(sql, params, Boolean.class));
    }

    private void create(Integer postId, byte[] bytes) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("content", bytes);

        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/image/add-image.sql");
        template.update(sql, namedParameters);

    }

    private void update(Integer postId, byte[] bytes) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId)
                .addValue("content", bytes);

        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/image/update-image.sql");
        template.update(sql, namedParameters);

    }

    @Override
    public void delete(Integer postId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId);

        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/image/delete-image.sql");
        template.update(sql, namedParameters);
    }

    @Override
    public Optional<byte[]> findByPostId(Integer postId) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId);

        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/image/get-image.sql");
        return Optional.ofNullable(template.queryForObject(sql, namedParameters, (rs, rowNum) -> rs.getBytes("content")));
    }

}
