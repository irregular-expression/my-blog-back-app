package ru.irrexp.practicum.dto;

import lombok.Builder;

import java.util.List;

public record PostsPageDto(List<PostDto> posts,
        boolean hasPrev,
        boolean hasNext,
        int lastPage) {

    @Builder
    public PostsPageDto {
    }

}
