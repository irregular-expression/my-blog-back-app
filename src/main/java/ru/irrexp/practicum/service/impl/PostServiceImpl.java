package ru.irrexp.practicum.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.irrexp.practicum.dao.PostDao;
import ru.irrexp.practicum.dto.CreatePostRq;
import ru.irrexp.practicum.dto.PostDto;
import ru.irrexp.practicum.dto.PostsPageDto;
import ru.irrexp.practicum.dto.UpdatePostRq;
import ru.irrexp.practicum.mapper.PostMapper;
import ru.irrexp.practicum.model.Post;
import ru.irrexp.practicum.service.PostService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostDao postDao;
    private final PostMapper mapper;

    @Override
    public PostsPageDto search(String query, Integer pageNumber, Integer pageSize) {
        String[] words = query.split(" ");
        StringBuilder title = new StringBuilder();
        Set<String> tags = new HashSet<>();
        for (String word : words) {
            if (word.startsWith("#")) {
                tags.add(word);
            } else if (title.isEmpty()) {
                title.append(word);
            } else {
                title.append(" ").append(word);
            }
        }
        return postDao.search(title.toString(), tags, pageNumber, pageSize);
    }

    @Override
    public Optional<PostDto> find(Integer postId) {
        return postDao.find(postId)
                .map(mapper::toDto);
    }

    @Override
    public PostDto create(CreatePostRq postRq) {
        Post post = postDao.save(mapper.toEntity(postRq));
        return mapper.toDto(post);
    }

    @Override
    public Optional<PostDto> update(Integer postId, UpdatePostRq updatePostRq) {
        return postDao.find(postId)
                .map(post -> confirmedUpdate(post, updatePostRq))
                .map(mapper::toDto);
    }

    @Override
    public boolean delete(Integer postId) {
        return postDao.find(postId)
                .map(this::confirmedDelete)
                .orElse(Boolean.FALSE);
    }

    private boolean confirmedDelete(@NonNull Post post) {
        postDao.delete(post.getId());
        return true;
    }

    private Post confirmedUpdate(@NonNull Post post, @NonNull UpdatePostRq updatePostRq) {
        return postDao.save(mapper.toEntity(post, updatePostRq));
    }


}
