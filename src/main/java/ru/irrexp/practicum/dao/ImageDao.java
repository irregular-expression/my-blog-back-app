package ru.irrexp.practicum.dao;

import java.util.Optional;

public interface ImageDao {

    void save(Integer postId, byte[] bytes);

    void delete(Integer postId);

    Optional<byte[]> findByPostId(Integer postId);

}
