package ru.irrexp.practicum.service;

import ru.irrexp.practicum.dto.CreatePostRq;
import ru.irrexp.practicum.dto.PostDto;
import ru.irrexp.practicum.dto.PostsPageDto;
import ru.irrexp.practicum.dto.UpdatePostRq;

import java.util.Optional;

public interface PostService {

    PostsPageDto search(String query, Integer pageNumber, Integer pageSize);

    Optional<PostDto> find(Integer postId);

    PostDto create(CreatePostRq postRq);

    Optional<PostDto> update(Integer postId, UpdatePostRq updatePostRq);

    boolean delete(Integer postId);

}
