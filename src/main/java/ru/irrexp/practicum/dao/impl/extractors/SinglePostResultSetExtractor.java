package ru.irrexp.practicum.dao.impl.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.irrexp.practicum.exception.ServerException;
import ru.irrexp.practicum.model.Post;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

public class SinglePostResultSetExtractor implements ResultSetExtractor<Optional<Post>> {

    @Override
    public Optional<Post> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Post p = null;

        while (rs.next()) {
            Integer postId = rs.getInt("id");

            if (p == null) {
                p = new Post();
                try {
                    p.setId(postId);
                    p.setTitle(rs.getString("title"));
                    p.setText(rs.getString("content"));
                    p.setCommentsCount(rs.getInt("commentscount"));
                    p.setLikesCount(rs.getInt("likescount"));
                    p.setTags(new HashSet<>());
                } catch (SQLException e) {
                    throw new ServerException(e.getLocalizedMessage());
                }
            }

            String tag = rs.getString("tag");
            if (!rs.wasNull()) {
                p.getTags().add(tag);
            }
        }

        return Optional.ofNullable(p);
    }

}
