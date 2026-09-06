package ru.irrexp.practicum.dao.impl.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.irrexp.practicum.dto.PostDto;
import ru.irrexp.practicum.dto.PostsPageDto;
import ru.irrexp.practicum.exception.ServerException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class PostsResultSetExtractor implements ResultSetExtractor<PostsPageDto> {

    @Override
    public PostsPageDto extractData(ResultSet rs) throws SQLException, DataAccessException {
        var pageBuilder = PostsPageDto.builder();
        Map<Integer, PostDto> postMap = new LinkedHashMap<>();
        boolean initialized = false;

        while (rs.next()) {
            if (!initialized) {
                int postsCount = rs.getInt("fullcount");
                int pageSize = rs.getInt("pagesize");
                int pageOffset = rs.getInt("pageoffset");

                int lastPage = (postsCount + pageSize - 1) / pageSize;
                boolean hasPrev = pageOffset > 0;
                boolean hasNext = (pageSize + pageOffset) <= postsCount;

                pageBuilder.lastPage(lastPage)
                     .hasNext(hasNext)
                     .hasPrev(hasPrev);
                initialized = true;
            }

            Integer postId = rs.getInt("id");

            PostDto post = postMap.computeIfAbsent(postId, id -> fillPostDto(rs, id));

            String tag = rs.getString("tag");
            if (!rs.wasNull()) {
                post.tags().add(tag);
            }
        }

        return pageBuilder
                .posts(postMap.values().stream().toList())
                .build();
    }

    private PostDto fillPostDto(ResultSet rs, Integer id) {
        var builder = PostDto.builder();
        try {
            builder.id(id)
                    .title(rs.getString("title"))
                    .text(rs.getString("content"))
                    .commentsCount(rs.getInt("commentscount"))
                    .likesCount(rs.getInt("likescount"))
                    .tags(new HashSet<>());

        } catch (SQLException e) {
            throw new ServerException(e.getLocalizedMessage());
        }
        return builder.build();
    }
}
