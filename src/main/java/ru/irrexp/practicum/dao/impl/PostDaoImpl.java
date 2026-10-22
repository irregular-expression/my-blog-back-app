package ru.irrexp.practicum.dao.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.irrexp.practicum.dao.PostDao;
import ru.irrexp.practicum.dao.TagDao;
import ru.irrexp.practicum.dto.PostsPageDto;
import ru.irrexp.practicum.model.Post;
import ru.irrexp.practicum.util.FileLoaderUtil;
import ru.irrexp.practicum.dao.impl.extractors.PostsResultSetExtractor;
import ru.irrexp.practicum.dao.impl.extractors.SinglePostResultSetExtractor;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;
import java.util.Set;

import static io.micrometer.common.util.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
@RequiredArgsConstructor
public class PostDaoImpl implements PostDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final TagDao tagDao;

    private final JsonMapper.Builder builder;

    @Override
    public Optional<Post> find(Integer postId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId);
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/find-post.sql");

        return namedParameterJdbcTemplate.query(sql, namedParameters, new SinglePostResultSetExtractor());
    }

    @Override
    @Transactional
    public Post save(Post post) {

        if (post.getId() == null) {
            createPost(post);
        } else {
            updatePost(post);
        }
        return post;
    }

    private void createPost(Post post) {
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/create-post.sql");
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("content", post.getText())
                .addValue("title", post.getTitle());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, namedParameters, keyHolder);

        Integer postId = Optional.ofNullable(keyHolder.getKeys())
                .map(k -> k.get("id"))
                .map(id -> (Integer) id)
                .orElseThrow(() -> new IllegalStateException("Не удалось получить сгенерированный ID созданного поста"));

        post.setId(postId);
        post.setLikesCount(0);
        post.setCommentsCount(0);

        if (!isEmpty(post.getTags())) {
            tagDao.addNewTags(post.getId(), post.getTags());
        }

    }

    private void updatePost(Post post) {
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/update-post.sql");
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("content", post.getText())
                .addValue("title", post.getTitle())
                .addValue("likesCount", post.getLikesCount())
                .addValue("commentsCount", post.getCommentsCount())
                .addValue("postId", post.getId());

        namedParameterJdbcTemplate.update(sql, namedParameters);

        tagDao.updateTags(post.getId(), Optional.ofNullable(post.getTags()).orElse(Set.of()));

    }

    @Override
    @Transactional
    public void delete(Integer postId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", postId);
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/delete-post.sql");

        namedParameterJdbcTemplate.update(sql, namedParameters);

        tagDao.deleteTags(postId);
    }

    @Override
    public PostsPageDto search(String titleSubstring, Set<String> tags, Integer pageNumber, Integer pageSize) {
        String sqlStart = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/search-posts-template-start.sql");
        String sqlEnd = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/search-posts-template-end.sql");

        StringBuilder builder = new StringBuilder(sqlStart);

        MapSqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("pageOffset", (pageNumber - 1) * pageSize)
                .addValue("pageSize", pageSize);

        if (isNotBlank(titleSubstring)) {
            namedParameters.addValue("titlePart", titleSubstring);
            builder.append("""
            \n      AND (:titlePart IS NULL OR p.title ILIKE '%' || :titlePart || '%')
            """);
        }

        if (!CollectionUtils.isEmpty(tags)) {
            namedParameters.addValue("tags", tags.stream().toList());
            builder.append("""
            \n      AND EXISTS (
                      SELECT 1
                      FROM blogtest.tags t2
                      WHERE t2.post_id = p.id
                      AND t2.tag IN (:tags)
                  )
            """);
        }
        builder.append(sqlEnd);

        return namedParameterJdbcTemplate.query(builder.toString(), namedParameters, new PostsResultSetExtractor());
    }

    @Override
    public void like(@NonNull Post post) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("postId", post.getId());
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/like.sql");
        namedParameterJdbcTemplate.update(sql, namedParameters);
        post.like();
    }

}
