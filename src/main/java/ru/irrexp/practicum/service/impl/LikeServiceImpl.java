package ru.irrexp.practicum.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.irrexp.practicum.dao.PostDao;
import ru.irrexp.practicum.model.Post;
import ru.irrexp.practicum.service.LikeService;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostDao dao;

    @Override
    @Transactional
    public boolean like(Integer postId) {
        return dao.find(postId)
                .map(this::confirmedLike)
                .orElse(Boolean.FALSE);
    }

    private boolean confirmedLike(@NonNull Post post) {
        dao.like(post);
        return true;
    }

}
