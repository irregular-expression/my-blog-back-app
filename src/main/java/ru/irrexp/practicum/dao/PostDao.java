package ru.irrexp.practicum.dao;

import lombok.NonNull;
import ru.irrexp.practicum.dto.PostsPageDto;
import ru.irrexp.practicum.model.Post;

import java.util.Optional;
import java.util.Set;

public interface PostDao {

    Optional<Post> find(Integer postId);

    Post save(Post post);

    void delete(Integer postId);

    PostsPageDto search(String titleSubstring, Set<String> tags, Integer pageNumber, Integer pageSize);

    void like(@NonNull Post post);

}
