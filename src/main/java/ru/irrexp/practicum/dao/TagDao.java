package ru.irrexp.practicum.dao;

import java.util.Set;

public interface TagDao {

    void addNewTags(Integer postId, Set<String> newTags);
    void updateTags(Integer postId, Set<String> tags);
    void deleteTags(Integer postId, Set<String> expiredTags);

    default void deleteTags(Integer postId) {
        deleteTags(postId, Set.of());
    }
}
