package ru.irrexp.practicum.dao.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.irrexp.practicum.dao.TagDao;
import ru.irrexp.practicum.util.FileLoaderUtil;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

@Repository
@RequiredArgsConstructor
public class TagDaoImpl implements TagDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void addNewTags(Integer postId, Set<String> newTags) {
        String addTagsSql = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/add-tag.sql");
        SqlParameterSource[] newTagsBatchParams = newTags.stream()
                .map(t -> new MapSqlParameterSource()
                        .addValue("postId", postId)
                        .addValue("tag", t))
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcTemplate.batchUpdate(addTagsSql, newTagsBatchParams);

    }

    @Override
    public void deleteTags(Integer postId, Set<String> expiredTags) {
        String sql = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/delete-tags.sql");

        MapSqlParameterSource expiredTagsParams = new MapSqlParameterSource()
                .addValue("postId", postId);

        if (!isEmpty(expiredTags)) {
            expiredTagsParams.addValue("tags", expiredTags);
            sql = String.format(sql, " AND tag IN (:tags)");
        } else {
            sql = String.format(sql, "");
        }

        namedParameterJdbcTemplate.update(sql, expiredTagsParams);

    }

    @Override
    public void updateTags(Integer postId, Set<String> tags) {

        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("postId", postId);

        String getTagsSql = FileLoaderUtil.loadStringFromClasspath("sql/queries/post/find-tags.sql");

        List<String> oldTags = namedParameterJdbcTemplate.queryForList(getTagsSql, params, String.class);

        Set<String> expiredTags = oldTags.stream()
                .filter(t -> !tags.contains(t))
                .collect(Collectors.toSet());
        if (!expiredTags.isEmpty()) {
            deleteTags(postId, expiredTags);
        }

        Set<String> newTags = tags.stream()
                .filter(t -> !oldTags.contains(t))
                .collect(Collectors.toSet());
        if (!newTags.isEmpty()) {
            addNewTags(postId, newTags);
        }
    }

}
